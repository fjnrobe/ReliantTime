package managers;

import com.mongodb.client.MongoDatabase;
import daos.DeductionDao;
import daos.LogDao;
import dtos.DeductionDto;
import dtos.ErrorDto;
import org.bson.types.ObjectId;
import utilities.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robertson_Laptop on 10/16/2016.
 */
public class DeductionManager {

    private final DeductionDao deductionDao;

    public DeductionManager(MongoDatabase reliantDb) {

        deductionDao = new DeductionDao(reliantDb);
    }

    public List<DeductionDto> getDeductionsByCriteria(String deductionCategory,
                                                      String deductionType,
                                                      String startDate,
                                                      String endDate)
    {
        return this.deductionDao.getDeductionsByCriteria(deductionCategory, deductionType, startDate, endDate);

    }

    public List<DeductionDto> getDeductionsByCategory(String categoryCode)
    {
        return this.deductionDao.getDeductionsByCategory(categoryCode);
    }

    public DeductionDto getDeductionById(ObjectId key)
    {
        return this.deductionDao.getDeductionById(key);
    }

    public List<ErrorDto> validateAndSaveDeduction(DeductionDto dto)
    {
        List<ErrorDto> errors = this.validateDeduction(dto);
        ObjectId id = null;
        if (errors.size() == 0)
        {
            if (dto.getId() == null)
            {
                id = this.addDeduction(dto);
            }
            else
            {
                this.updateDeduction(dto);
                id = dto.getId();
            }
        }

        return errors;
    }

    private List<ErrorDto> validateDeduction(DeductionDto dto)
    {
        List<ErrorDto> errors = new ArrayList<ErrorDto>();
        if (StringUtils.isEmpty(dto.getDeductionCategory()))
        {
            errors.add(new ErrorDto("Deduction Category is required"));
        }
        if (StringUtils.isEmpty(dto.getDeductionType()))
        {
            errors.add(new ErrorDto("Category Type is required"));
        }
        if (dto.getAmount() <= 0)
        {
            errors.add(new ErrorDto("Deduction Amount must be > 0"));
        }

        return errors;
    }

    public ObjectId addDeduction(DeductionDto dto)
    {
        return this.deductionDao.addDeduction(dto);
    }

    public void deleteDeduction (ObjectId key)
    {
        this.deductionDao.deleteDeduction(key);
    }

    public void updateDeduction (DeductionDto dto)
    {
        this.deductionDao.updateDeduction(dto);
    }


}
