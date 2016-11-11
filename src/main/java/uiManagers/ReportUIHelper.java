package uiManagers;

import dtos.InnotasRowUiDto;
import dtos.LogDto;
import utilities.DateTimeUtils;
import utilities.NumberUtils;
import utilities.SortUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Robertson_Laptop on 9/7/2016.
 */
public class ReportUIHelper {

    //this method will create a series of rows for the Innotas weekly status report:
    //{description}  [array 1..7] to hold the hours for day 1 (Monday) thru 7 (Friday)
    public static List<InnotasRowUiDto> createInnotasReportMatrix(List<LogDto> entries)
    {
        HashMap<String, InnotasRowUiDto> matrix = new HashMap<String, InnotasRowUiDto>();

        for (LogDto entry : entries)
        {
            if (!matrix.containsKey(entry.getPrimaveraDesc()))
            {
                InnotasRowUiDto newRow = new InnotasRowUiDto();
                newRow.setDescription(entry.getPrimaveraDesc());
                newRow.setLogDate(entry.getLogDate());
                for (int x = 0; x <= 6; x++)
                {
                    newRow.getHoursPerDay()[x] = 0;
                }
                matrix.put(entry.getPrimaveraDesc(), newRow);
            }
            InnotasRowUiDto dto = matrix.get(entry.getPrimaveraDesc());

            //determine which day of the week the current entry is for
            int dayOfWeek = DateTimeUtils.getDayOfWeek(entry.getLogDate());

            dto.getHoursPerDay()[dayOfWeek- 2] = NumberUtils.roundHours(entry.getHours());
        }

        ArrayList tmp = new ArrayList<InnotasRowUiDto>(matrix.values());
        SortUtils.sortInnotasRowUiDto(tmp, true);
        return tmp;
    }
}
