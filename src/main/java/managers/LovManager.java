package managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import common.FieldConstants;
import common.LOVEnum;
import daos.LovDao;
import dtos.*;
import org.bson.Document;
import utilities.SortUtils;
import utilities.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robertson_Laptop on 6/18/2016.
 */
public class LovManager {

    private LovDao lovDao;
    private SirPcrManager sirPcrManager;
    private LogManager logManager;

    public LovManager(MongoDatabase db)
    {
        this.lovDao = new LovDao(db);
    }

    //inject other managers
    public void setSirPcrManager(SirPcrManager manager)
    {
        this.sirPcrManager = manager;
    }

    public void setLogManager(LogManager manager)
    {
        this.logManager = manager;
    }


    //returns a unique list of all lov code entries (the codes)
    public List<String> getLovList()
    {
        return this.lovDao.getLovList();
    }

    public boolean isInUse(LOVEnum lovCode, String lovValue)
    {
        boolean inUse = false;
        switch (lovCode)
        {
            case DEDUCTION_CATEGORY:

                break;
            case DEDUCTION_TYPE:

                break;
            case PRIMAVERA_TYPE:

                inUse = this.logManager.getUsageCount(FieldConstants.PRIMAVERA_DESC, lovValue ) > 0;

                break;
            case SUB_PROCESS_TYPE:
                inUse = this.sirPcrManager.getUsageCount(FieldConstants.SUBPROCESS_DESC, lovValue) > 0;

            case ACTIVITY_TYPE:

               inUse = this.logManager.getUsageCount(FieldConstants.ACTIVITY_DESC, lovValue) > 0;
                break;
        }

        return inUse;
    }

    public List<LovBaseDto> getLovEntries(LOVEnum lovCode)
    {
        List<LovBaseDto> dtos = this.lovDao.getLovEntries(lovCode);

        return SortUtils.sortLovValues(dtos,true);
    }

    /**
     * this method returns all the lov entries (deductionType) where the incoming deduction
     * code = lov.deductionCode
     *
     * @param deductionCode - code (HEALTH, RET, TAX, etc) to filter the deduction types on
     * @return - list of the lov entries where deductionTypeCode = incoming deductionCode
     */
    public List<LovBaseDto> getLovEntriesByDeductionCode(String deductionCode)
    {
        List<LovBaseDto> dtos = this.lovDao.getLovEntriesByDeductionCode(deductionCode);

        return SortUtils.sortLovValues(dtos,true);
    }

    public void updateLovEntry(LOVEnum lovCode, String oldDescription, String newDescription)
    {
        this.lovDao.updateLovEntry(lovCode, oldDescription, newDescription);

        //the lov description is de-normalized across the sirPcr and log collection,
        //so we need to update those values as well

        switch (lovCode)
        {
            case PRIMAVERA_TYPE:
            case ACTIVITY_TYPE:

                this.logManager.updateLovEntry(lovCode, oldDescription, newDescription);
                break;

            case SUB_PROCESS_TYPE:
                this.sirPcrManager.updateLovEntry(lovCode, oldDescription, newDescription);

        }
    }

    public void deleteLovEntry(LOVEnum lovCode, String oldDescription) {

        this.lovDao.deleteLovEntry(lovCode, oldDescription);
    }

    public List<ErrorDto> validateAndSaveLovEntry(LOVEnum lovEnum, String value) {
        List<ErrorDto> errors = this.validateLovEntry(lovEnum, value);

        if (errors.isEmpty()) {
            this.lovDao.addLovEntry(lovEnum, value);
        }

        return errors;
    }

    private List<ErrorDto> validateLovEntry(LOVEnum lovEnum, String value)
    {
        List<ErrorDto> errors = new ArrayList<ErrorDto>();
        if (StringUtils.isEmpty(value))
        {
            errors.add(new ErrorDto("LOV Description is missing"));
        }

        else {
            List<LovBaseDto> existingEntries = this.lovDao.getLovEntries(lovEnum);
            for (LovBaseDto existingEntry : existingEntries) {
                if (value.toLowerCase().equals(existingEntry.getLovDescription().toLowerCase()))
                {
                    errors.add(new ErrorDto("This LOV entry already exists"));
                    break;
                }
            }
        }

        return errors;
    }
}
