package uiManagers;

import common.LOVEnum;
import common.MonthYear;
import dtos.*;
import managers.FinancialManager;
import managers.LovManager;
import org.bson.types.ObjectId;
import spark.Request;
import utilities.DateTimeUtils;
import utilities.NumberUtils;
import utilities.SortUtils;
import utilities.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Robertson_Laptop on 10/25/2016.
 */
public class FinancialUIHelper {

    public static PurchaseOrderDto mapPoFromQueryParams(Request request)
    {
        PurchaseOrderDto dto = new PurchaseOrderDto();
        if (!StringUtils.isEmpty(request.queryParams("id")))
        {
            dto.setId(new ObjectId(request.queryParams("id")));
        }

        dto.setPoNumber(request.queryParams("poNumber"));
        dto.setPoTitle(request.queryParams("poTitle"));
        try
        {
            dto.setTotalHours(Double.parseDouble(request.queryParams("poHours")));
        } catch (Exception e)
        {
            dto.setTotalHours(0.0);
        }

        try
        {
            dto.setHourlyRate(Double.parseDouble(request.queryParams("hourlyRate")));
        } catch (Exception e)
        {
            dto.setHourlyRate(0.0);
        }

        try
        {
            dto.setPassthruRate(Double.parseDouble(request.queryParams("passthruRate")));
        } catch (Exception e)
        {
            dto.setPassthruRate(0.0);
        }
        try
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            dto.setStartDate(df.parse(request.queryParams("startDate")));
        } catch (Exception e)
        {
            dto.setStartDate(DateTimeUtils.getNullDate());
        }
        try
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            dto.setEndDate(df.parse(request.queryParams("endDate")));
        } catch (Exception e)
        {
            dto.setEndDate(DateTimeUtils.getNullDate());
        }

        return dto;
    }
    public static InvoiceDto mapFromQueryParams(Request request)
    {
        InvoiceDto dto = new InvoiceDto();

        if (!StringUtils.isEmpty(request.queryParams("id")))
        {
            dto.setId(new ObjectId(request.queryParams("id")));
        }
        else if (!StringUtils.isEmpty(request.queryParams("delId")))
        {
            dto.setId(new ObjectId(request.queryParams("delId")));
        }

        dto.setPoNumber(request.queryParams("poNumber"));
        dto.setInvoiceNumber(request.queryParams("invoiceNumber"));

        if (!StringUtils.isEmpty(request.queryParams("monthYear")))
        {

            String monthYear = request.queryParams("monthYear").substring(0,4) +
                                request.queryParams("monthYear").substring(5);

            dto.setMonthYear(new MonthYear(monthYear));
        }

        try {
            dto.setHours(Double.parseDouble(request.queryParams("hours")));
        } catch (Exception e)
        {
            dto.setHours(0.0);
        }


        try {
            dto.setTotalGross(Double.parseDouble(request.queryParams("gross")));
        } catch (Exception e)
        {
            dto.setTotalGross(0.0);
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        try {
            dto.setInvoiceDate(df.parse(request.queryParams("invDate")));
        } catch (Exception e)
        {
            dto.setInvoiceDate(DateTimeUtils.getNullDate());
        }

        try {
            dto.setReceivedDate(df.parse(request.queryParams("recvDate")));
        } catch (Exception e)
        {
            dto.setReceivedDate(DateTimeUtils.getNullDate());
        }

        return dto;
    }

    public static HashMap<String, Object> createPoSummary(List<PurchaseOrderDto> allPos,
                                                          FinancialManager financialManager,
                                                          String poNumber)
    {
        HashMap<String, Object> root = new HashMap<String, Object>();

        //to be used for the dropdown
        root.put("poList", allPos);

        //if there is an active PO, default to that one
        for (PurchaseOrderDto allPo : allPos)
        {
            if ( (!StringUtils.isEmpty(poNumber) && allPo.getPoNumber().equals(poNumber)) ||
                    (StringUtils.isEmpty(poNumber) && DateTimeUtils.isDateBetween(DateTimeUtils.getSystemDate(),
                    allPo.getStartDate(), allPo.getEndDate())))
            {
                root.put("currentPoNumber", allPo.getPoNumber());
                root.put("poDto", allPo);

                //get the invoices associated with the PO
                FinancialSearchCriteriaDto searchDto = new FinancialSearchCriteriaDto();
                searchDto.setPoNumber(allPo.getPoNumber());
                List<InvoiceDto> poInvoices =
                        financialManager.getInvoicesByCriteria(searchDto);

                root.put("invoices", SortUtils.sortFinancialDto(poInvoices, true));

                //now add all the calced totals
                double totalGross = allPo.getHourlyRate() * allPo.getTotalHours();
                double totalPassthru = allPo.getPassthruRate() * allPo.getTotalHours();

                root.put("totalHours", allPo.getTotalHours());
                root.put("totalGross", totalGross);
                root.put("totalPassthru", totalPassthru);
                root.put("totalAdjustedGross", totalGross - totalPassthru);

                double billedHours = 0.0;
                totalGross = 0.0;
                totalPassthru = 0.0;
                for (InvoiceDto invoice : poInvoices)
                {
                    billedHours += invoice.getHours();
                    totalGross += invoice.getTotalGross();
                    totalPassthru += (invoice.getHours() * allPo.getPassthruRate() );

                }
                root.put("billedHours", billedHours);
                root.put("billedGross", totalGross);
                root.put("billedPassthru", totalPassthru);
                root.put("billedAdjustedGross", totalGross - totalPassthru);

                double dueHours = allPo.getTotalHours() - billedHours;
                double dueGross = dueHours * allPo.getHourlyRate();
                double duePassthru = dueHours * allPo.getPassthruRate();
                root.put("dueHours", dueHours);
                root.put("dueGross", dueGross);
                root.put("duePassthru", duePassthru);
                root.put("dueAdjustedGross", dueGross - duePassthru);
            }
        }

        if (!root.containsKey("poDto"))
        {
            root.put("poDto", new PurchaseOrderDto());
            root.put("currentPoNumber", "");
        }
        return root;
    }

    public static List<InvoiceDto> createRevenueFinancialSummary(List<InvoiceDto> dtos)
    {
        //the key will be year of the posting
        HashMap<String, InvoiceDto> map = new HashMap<String, InvoiceDto>();
        List<InvoiceDto> retList = new ArrayList<InvoiceDto>();

       for (InvoiceDto dto : dtos) {

            String key = String.valueOf(dto.getMonthYear().getYear()) + "01";

            InvoiceDto entry = null;
            if (map.containsKey(key)) {
                entry = map.get(key);
            } else {
                entry = new InvoiceDto();
                entry.setMonthYear(new MonthYear(key));

                entry.setHours(0.0);
                entry.setTotalGross(0.0);
                map.put(key, entry);
            }

            entry.setHours(entry.getHours() + dto.getHours());
            entry.setTotalGross(entry.getTotalGross() + dto.getTotalGross());

        }

        for (String key : map.keySet())
        {
            retList.add(map.get(key));
        }

        SortUtils.sortFinancialDto(retList, false);

        return retList;
    }

    public static  FinancialSummaryDto createFinancialSummary(LovManager lovManager,
                                                      String parmYear,
                                                      List<InvoiceDto> invoiceDtos,
                                                      List<DeductionDto> deductionDtos) {
        int year = Integer.parseInt(parmYear);
        FinancialSummaryDto retDto = new FinancialSummaryDto();

        retDto.setCurrentYear(parmYear);

        //summarize the revenue for the year
        for (InvoiceDto dto : invoiceDtos) {
            if (dto.getMonthYear().getYear() == year)
            {
                retDto.setBilledHours(retDto.getBilledHours() + dto.getHours());
                retDto.setBilledGross(retDto.getBilledGross() + dto.getTotalGross());

                if (!dto.getReceivedDate().equals(DateTimeUtils.getNullDate()))
                {
                    retDto.setReceivedHours(retDto.getReceivedHours() + dto.getHours());
                    retDto.setReceivedGross(retDto.getReceivedGross() + dto.getTotalGross());
                }
                else
                {
                    retDto.setDueHours(retDto.getDueHours() + dto.getHours());
                    retDto.setDueGross(retDto.getDueGross() + dto.getTotalGross());
                }
            }

            //update the overall gross for the year
            FinancialSummaryByYearUIDto yearDto = null;
            for (int x = 0; x < retDto.getSummaryByYear().size(); x++) {
                if (retDto.getSummaryByYear().get(x).getYear().equals( String.valueOf(dto.getMonthYear().getYear()))) {
                    yearDto = retDto.getSummaryByYear().get(x);
                    break;
                }
            }
            if (yearDto == null)
            {
                yearDto = new FinancialSummaryByYearUIDto();
                yearDto.setYear( String.valueOf(dto.getMonthYear().getYear()));
                retDto.getSummaryByYear().add(yearDto);
            }
            yearDto.setGrossIncome(yearDto.getGrossIncome() +
                                    dto.getTotalGross());
            yearDto.setGrossHours(yearDto.getGrossHours() + dto.getHours());
        }

        List<LovBaseDto> categoryList = lovManager.getLovEntries(LOVEnum.DEDUCTION_CATEGORY);
        List<LovBaseDto> typeList = lovManager.getLovEntries(LOVEnum.DEDUCTION_TYPE);

        //the key will be category desc/type
        HashMap<String, DeductionUiDto> map = new HashMap<String, DeductionUiDto>();
        List<DeductionUiDto> retList = new ArrayList<DeductionUiDto>();

        //summary the expenses by category / type
        for (DeductionDto dto : deductionDtos) {

            SimpleDateFormat ft =
                    new SimpleDateFormat ("yyyy");

            String deductionYear = ft.format(dto.getPostDate());

            if (deductionYear.equals(parmYear)) {
                String key = dto.getDeductionCategory() +
                        dto.getDeductionType();

                DeductionUiDto entry = null;
                if (map.containsKey(key)) {
                    entry = map.get(key);
                } else {
                    entry = new DeductionUiDto();

                    for (LovBaseDto lovDto : categoryList) {
                        DeductionCategory deductionCategory = (DeductionCategory) lovDto;

                        if (dto.getDeductionCategory().equals(deductionCategory.getDeductionCode())) {
                            entry.setDeductionCategoryDesc(deductionCategory.getLovDescription());
                            break;
                        }
                    }

                    for (LovBaseDto lovDto : typeList) {
                        DeductionTypeDto deductionTypeDto = (DeductionTypeDto) lovDto;

                        if (dto.getDeductionType().equals(deductionTypeDto.getDeductionTypeCode())) {
                            entry.setDeductionTypeDesc(deductionTypeDto.getLovDescription());
                        }
                    }

                    entry.getDeductionDto().setAmount(0.0);
                    map.put(key, entry);
                }

                entry.getDeductionDto().setAmount(entry.getDeductionDto().getAmount() + dto.getAmount());
            }

            FinancialSummaryByYearUIDto yearDto = null;

            //update the overall deductions for the year
            for (int x = 0; x < retDto.getSummaryByYear().size(); x++) {
                if (retDto.getSummaryByYear().get(x).getYear().equals(deductionYear)) {
                    yearDto = retDto.getSummaryByYear().get(x);
                    break;
                }
            }
            if (yearDto == null)
            {
                yearDto = new FinancialSummaryByYearUIDto();
                yearDto.setYear( deductionYear);
                retDto.getSummaryByYear().add(yearDto);

            }
            yearDto.setGrossDeductions (yearDto.getGrossDeductions() +
                    dto.getAmount());
        }

        for (String key : map.keySet()) {
            retList.add(map.get(key));
        }

        //round the totals to 2 digits
        retDto.setBilledHours(NumberUtils.roundHours(retDto.getBilledHours() ));
        retDto.setBilledGross(NumberUtils.roundDollars(retDto.getBilledGross()));
        retDto.setReceivedHours(NumberUtils.roundHours(retDto.getReceivedHours()));
        retDto.setReceivedGross(NumberUtils.roundDollars(retDto.getReceivedGross()));
        retDto.setDueHours(NumberUtils.roundHours(retDto.getDueHours()));
        retDto.setDueGross(NumberUtils.roundDollars(retDto.getDueGross()));

        for (DeductionUiDto dto : retList)
        {
            dto.getDeductionDto().setAmount(NumberUtils.roundDollars(dto.getDeductionDto().getAmount()));
        }

        retDto.setExpenseList(SortUtils.sortDeductionUiDto(retList, true));

        //round the yearly totals and then sort by year
        for (FinancialSummaryByYearUIDto dto : retDto.getSummaryByYear())
        {
            dto.setGrossHours(NumberUtils.roundHours(dto.getGrossHours()));
            dto.setGrossDeductions(NumberUtils.roundDollars(dto.getGrossDeductions()));
            dto.setGrossIncome(NumberUtils.roundDollars(dto.getGrossIncome()));
            dto.setNetIncome(dto.getGrossIncome() - dto.getGrossDeductions());
        }

        SortUtils.sortFinancialSummaryByYearUIDto(retDto.getSummaryByYear(), false);
        return retDto;
    }

    public static  List<String> createYearsWithInvoices( List<InvoiceDto> allInvoices)
    {
        HashMap<String, String> years= new HashMap<String, String>();
        ArrayList<String> retYears = new ArrayList<String>();

        for (InvoiceDto allInvoice : allInvoices)
        {
            if (!years.containsKey(allInvoice.getMonthYear().getYearName()))
            {
                years.put(allInvoice.getMonthYear().getYearName(), allInvoice.getMonthYear().getYearName());
            }
        }

        for (String year : years.keySet())
        {
            retYears.add(year.toString());
        }

        Collections.sort(retYears);
        Collections.reverse(retYears);

        return retYears;

    }

}
