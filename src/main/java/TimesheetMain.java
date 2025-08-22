import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import models.Timesheet;
import services.TimesheetSelectionService;

import java.io.File;
import java.util.List;

public class TimesheetMain {
    public static void main(String[] args) {
        try {

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());


            File file = new File("src/test/resources/timesheets.json");
            List<Timesheet> allTimesheets = mapper.readValue(file, new TypeReference<List<Timesheet>>() {});


            TimesheetSelectionService service = new TimesheetSelectionService();
            List<Timesheet> selected = service.selectTimesheets(allTimesheets);


            //System.out.println("Selected Timesheets (" + allTimesheets.size() + "):");
            for(Timesheet timesheet: selected) {
                System.out.println(timesheet);
            }

            //allTimesheets.forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}