package services;

import models.Timesheet;

import java.util.*;
import java.util.stream.Collectors;

public class TimesheetSelectionService {

    private static final int BATCH_SIZE = 150;

    public List<Timesheet> selectTimesheets(List<Timesheet> allTimesheets) {

        if (allTimesheets == null || allTimesheets.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Queue<Timesheet>> agencyMap = allTimesheets.stream()
                .sorted(Comparator.comparing(ts -> ts.createdDate))
                .collect(Collectors.groupingBy(
                        ts -> ts.agencyId,
                        LinkedHashMap::new,
                        Collectors.toCollection(LinkedList::new)
                ));

        /*for (Map.Entry<Long, Queue<Timesheet>> entry : agencyMap.entrySet()) {
            System.out.println("Key-Agency ID): " + entry.getKey());
            //System.out.println("Timesheets Details: " + entry.getValue());
        }*/

        List<Timesheet> resultTimesheet = new ArrayList<>(BATCH_SIZE);

        for (Queue<Timesheet> timesheetQueue : agencyMap.values()) {
            //System.out.println(timesheetQueue);
            if (!timesheetQueue.isEmpty() && resultTimesheet.size() < BATCH_SIZE) {
                resultTimesheet.add(timesheetQueue.poll());
            }
        }

        while (resultTimesheet.size() < BATCH_SIZE) {
            boolean flagAdd = false;

            for (Queue<Timesheet> queue : agencyMap.values()) {
                if (resultTimesheet.size() >= BATCH_SIZE) break;
                if (!queue.isEmpty()) {
                    resultTimesheet.add(queue.poll());
                    flagAdd = true;
                }
            }

            if (!flagAdd) {
                break;
            }
        }

        return resultTimesheet;
    }

}
