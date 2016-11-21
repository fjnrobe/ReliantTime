package managers;

import com.mongodb.client.MongoDatabase;
import common.MonthYear;
import daos.InvoiceDao;
import daos.LogDao;
import daos.PurchaseOrderDao;
import dtos.*;
import org.bson.types.ObjectId;
import utilities.DateTimeUtils;
import utilities.SortUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robertson_Laptop on 10/25/2016.
 */
public class FinancialManager {

    private InvoiceDao invoiceDao;
    private PurchaseOrderDao purchaseOrderDao;
    private final LogDao logDao;


    public FinancialManager(MongoDatabase reliantDb) {

        invoiceDao = new InvoiceDao(reliantDb);
        purchaseOrderDao = new PurchaseOrderDao(reliantDb);
        logDao = new LogDao(reliantDb);
    }


    public InvoiceDto getNextInvoice(MonthYear monthYear)
    {
        InvoiceDto nextInvoice = new InvoiceDto();
        PurchaseOrderDto purchaseOrderDto =
                this.purchaseOrderDao.getPurchaseOrderByMonthYear(monthYear);

        nextInvoice.setMonthYear(monthYear);
        nextInvoice.setPoNumber(purchaseOrderDto.getPoNumber());
        nextInvoice.setInvoiceNumber("RSLLC-" + monthYear.getMonthYear());

        List<CalendarDto> data = logDao.getCalendarHoursByMonth(monthYear);
        double totHours = 0.0;
        for (CalendarDto dto : data)
        {
            totHours += dto.getHours();
        }
        nextInvoice.setHours(totHours);
        nextInvoice.setTotalGross(totHours * purchaseOrderDto.getHourlyRate());
        nextInvoice.setInvoiceDate(DateTimeUtils.getSystemDate());

        return nextInvoice;
    }

    public List<PurchaseOrderDto> getAllPurchaseOrders()
    {
        return this.purchaseOrderDao.getAll();
    }

    public List<ErrorDto> validateAndSavePO(PurchaseOrderDto dto)
    {
        List<ErrorDto> errors = this.validatePO(dto);

        if (errors.size() == 0)
        {
            if (dto.getId() == null)
            {
                this.purchaseOrderDao.addPurchaseOrder(dto);
            }
            else
            {
                this.purchaseOrderDao.updatePurchaseOrder(dto);
            }
        }

        return errors;
    }

    private List<ErrorDto> validatePO(PurchaseOrderDto dto)
    {
        List<ErrorDto> errorDtos = new ArrayList<ErrorDto>();

        //most validations are required fields which are set on the page
        if (dto.getStartDate().after(dto.getEndDate()))
        {
            errorDtos.add(new ErrorDto("The PO Start Date must be before the End Date"));
        }

        //if there are existing invoices - make sure the start date/end date didn't
        //leave orphans
        FinancialSearchCriteriaDto searchDto = new FinancialSearchCriteriaDto();
        searchDto.setPoNumber(dto.getPoNumber());
        List<InvoiceDto> invoiceDtos =
                this.invoiceDao.getInvoicesByCriteria(searchDto);

        if (invoiceDtos != null)
        {
            for (InvoiceDto invoiceDto : invoiceDtos)
            {
                MonthYear myStart = DateTimeUtils.getMonthYear(dto.getStartDate());
                MonthYear myEnd = DateTimeUtils.getMonthYear(dto.getEndDate());
                if ((myStart.compareTo(invoiceDto.getMonthYear()) > 0) ||
                        (myEnd.compareTo(invoiceDto.getMonthYear()) < 0)) {

                    errorDtos.add(new ErrorDto("There are invoices outside of the Start Date / End Date range"));
                }
            }
        }
        return errorDtos;
    }

    public List<ErrorDto> validateAndSaveInvoice(InvoiceDto dto)
    {
        List<ErrorDto> errorsDto = this.validateInvoice(dto);

        if (errorsDto.size() == 0) {
            if (dto.getId() == null) {
                this.addInvoice(dto);
            } else {
                this.updateInvoice(dto);
            }
        }

        return errorsDto;
    }

    private List<ErrorDto> validateInvoice(InvoiceDto dto)
    {
        List<ErrorDto> errorDtos = new ArrayList<ErrorDto>();

        //for now, the requirement fields are checked on the page.
        FinancialSearchCriteriaDto searchDto = new FinancialSearchCriteriaDto();
        searchDto.setInvoiceNumber(dto.getInvoiceNumber());

        List<InvoiceDto> dtos = this.getInvoicesByCriteria(searchDto);

        InvoiceDto existingInvoiceDto = null;

        //check that the invoice isn't a dup of another invoice number
        if ((dtos.size() > 0) && !dtos.get(0).getId().equals(dto.getId()))
        {
            errorDtos.add(new ErrorDto("This invoice number has already been used"));
        }
        //if this is a dup - then the above edit would hit. if there is more
        //than 0, it means we are editing an existing invoice.
        else if (dtos.size() > 0)
        {
            existingInvoiceDto = dtos.get(0);
        }

        PurchaseOrderDto purchaseOrderDto = this.purchaseOrderDao.getPurchaseOrderByPONumber(dto.getPoNumber());
        if (purchaseOrderDto == null) {
            errorDtos.add(new ErrorDto("This PO Number is not valid"));
        }
        else
        {
            //we need to insure that the remaining balance on the PO covers the
            //incoming invoice gross
            double availablePOHours = purchaseOrderDto.getTotalHours() - purchaseOrderDto.getTotalHoursBilled();

            //if the hours were adjusted - we need to add back the original hours and then subtract
            //the new hours
            if (existingInvoiceDto != null)
            {
                availablePOHours += existingInvoiceDto.getHours();
            }
            if (availablePOHours < dto.getHours())
            {
                errorDtos.add(new ErrorDto("There are only " + availablePOHours + " remaining on the PO."));
            }

            //make sure the invoice monthyear are within the PO range
            if (!DateTimeUtils.isDateBetween(DateTimeUtils.convertMonthYear(dto.getMonthYear()),
                    purchaseOrderDto.getStartDate(), purchaseOrderDto.getEndDate()))
            {
                errorDtos.add(new ErrorDto("This invoice is not within the purchase order effective date range"));
            }
        }

        return errorDtos;
    }

    public List<InvoiceDto> getInvoicesByCriteria(FinancialSearchCriteriaDto searchDto)
    {
        return invoiceDao.getInvoicesByCriteria(searchDto);
    }

    public List<InvoiceDto> getAllInvoices()
    {
        List<InvoiceDto> dtos = this.invoiceDao.getAllInvoices();

        return SortUtils.sortFinancialDto(dtos, false);
    }

    public ObjectId addInvoice(InvoiceDto dto) {

        PurchaseOrderDto purchaseOrderDto = this.purchaseOrderDao.
                getPurchaseOrderByPONumber(dto.getPoNumber());

        dto.setTotalGross(dto.getHours() * (purchaseOrderDto.getHourlyRate() - purchaseOrderDto.getPassthruRate()));

        //update the running amounts/dollars on the invoice and the po
        dto.setPriorHoursRemaining(purchaseOrderDto.getTotalHours() - purchaseOrderDto.getTotalHoursBilled());
        dto.setPriorAmtRemaining(dto.getPriorHoursRemaining() * (purchaseOrderDto.getHourlyRate() - purchaseOrderDto.getPassthruRate()));

        purchaseOrderDto.setTotalHoursBilled(purchaseOrderDto.getTotalHoursBilled() +
                                                dto.getHours());

        dto.setHoursRemaining(purchaseOrderDto.getTotalHours() - purchaseOrderDto.getTotalHoursBilled());
        dto.setAmtRemaining(dto.getHoursRemaining() * (purchaseOrderDto.getHourlyRate() - purchaseOrderDto.getPassthruRate()));

        this.purchaseOrderDao.updatePurchaseOrder(purchaseOrderDto);

        ObjectId id = invoiceDao.addInvoice(dto);
        dto.setId(id);
        //now, roll forward to update any later effective invoice hours/amt remaining
        this.updateInvoiceBalances(dto);

        return id;
    }

    public void updateInvoice(InvoiceDto dto) {

        PurchaseOrderDto purchaseOrderDto = this.purchaseOrderDao.
                getPurchaseOrderByPONumber(dto.getPoNumber());

        //get the original invoice - so we can back out the amount/hours
        InvoiceDto origDto = this.invoiceDao.getInvoiceById(dto.getId());

        //back out the original amounts
        purchaseOrderDto.setTotalHoursBilled(purchaseOrderDto.getTotalHoursBilled() - origDto.getHours());

        dto.setTotalGross(dto.getHours() * (purchaseOrderDto.getHourlyRate() - purchaseOrderDto.getPassthruRate()));

        //update the running amounts/dollars on the invoice and the po
        dto.setPriorHoursRemaining(purchaseOrderDto.getTotalHours() - purchaseOrderDto.getTotalHoursBilled());
        dto.setPriorAmtRemaining(dto.getPriorHoursRemaining() * (purchaseOrderDto.getHourlyRate() - purchaseOrderDto.getPassthruRate()));

        purchaseOrderDto.setTotalHoursBilled(purchaseOrderDto.getTotalHoursBilled() +
                dto.getHours());

        dto.setHoursRemaining(purchaseOrderDto.getTotalHours() - purchaseOrderDto.getTotalHoursBilled());
        dto.setAmtRemaining(dto.getHoursRemaining() * (purchaseOrderDto.getHourlyRate() - purchaseOrderDto.getPassthruRate()));

        this.purchaseOrderDao.updatePurchaseOrder(purchaseOrderDto);

        this.invoiceDao.updateInvoice(dto);

        //now, roll forward to update any later effective invoice hours/amt remaining
        this.updateInvoiceBalances(dto);
    }

    public void deleteInvoice(ObjectId id)
    {
        //get the original invoice - so we can back out the amount/hours
        InvoiceDto origDto = this.invoiceDao.getInvoiceById(id);

        PurchaseOrderDto purchaseOrderDto = this.purchaseOrderDao.
                getPurchaseOrderByPONumber(origDto.getPoNumber());

        if (purchaseOrderDto != null) {
            //back out the original amounts
            purchaseOrderDto.setTotalHoursBilled(purchaseOrderDto.getTotalHoursBilled() - origDto.getHours());

            this.purchaseOrderDao.updatePurchaseOrder(purchaseOrderDto);
        }
        this.invoiceDao.deleteInvoice(id);
    }

    private void updateInvoiceBalances(InvoiceDto currentDto)
    {
        FinancialSearchCriteriaDto searchCriteriaDto = new FinancialSearchCriteriaDto();
        searchCriteriaDto.setPoNumber(currentDto.getPoNumber());

        PurchaseOrderDto purchaseOrderDto =
                this.purchaseOrderDao.getPurchaseOrderByPONumber(currentDto.getPoNumber());

        List<InvoiceDto> allInvoiceDtos = this.getInvoicesByCriteria(searchCriteriaDto);

        SortUtils.sortFinancialDto(allInvoiceDtos, true);

        boolean found = false;
        InvoiceDto priorInvoiceDto = null;
        //find the entry for the currentDto - we will then update all invoices effective later
        for (InvoiceDto invoiceDto : allInvoiceDtos)
        {

            //if there is no prior invoice, it means we are on the first invoice
            if (priorInvoiceDto == null) {

                invoiceDto.setTotalGross(invoiceDto.getHours() * (purchaseOrderDto.getHourlyRate() -
                        purchaseOrderDto.getPassthruRate()));

                invoiceDto.setPriorHoursRemaining(purchaseOrderDto.getTotalHours());
                invoiceDto.setPriorAmtRemaining(purchaseOrderDto.getTotalHours() * (purchaseOrderDto.getHourlyRate() - purchaseOrderDto.getPassthruRate()));

                invoiceDto.setHoursRemaining(purchaseOrderDto.getTotalHours() - invoiceDto.getHours());
                invoiceDto.setAmtRemaining(invoiceDto.getPriorAmtRemaining() - invoiceDto.getTotalGross());
            }
            else
            {
                invoiceDto.setTotalGross(invoiceDto.getHours() * (purchaseOrderDto.getHourlyRate() -
                        purchaseOrderDto.getPassthruRate()));
                invoiceDto.setPriorAmtRemaining(priorInvoiceDto.getAmtRemaining());
                invoiceDto.setPriorHoursRemaining(priorInvoiceDto.getHoursRemaining());
                invoiceDto.setHoursRemaining(priorInvoiceDto.getHoursRemaining() - invoiceDto.getHours());
                invoiceDto.setAmtRemaining(priorInvoiceDto.getAmtRemaining() - invoiceDto.getTotalGross());
            }
            this.invoiceDao.updateInvoice(invoiceDto);

            priorInvoiceDto = invoiceDto;

        }
    }
}
