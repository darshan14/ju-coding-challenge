package services;

import models.Timesheet;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimesheetSelectionServiceTest {

    private final TimesheetSelectionService service = new TimesheetSelectionService();

    private Timesheet ts(long agencyId, long timesheetId, long workerId, String date) {
        Timesheet t = new Timesheet();
        t.agencyId = agencyId;
        t.timesheetId = timesheetId;
        t.workerId = workerId;
        t.createdDate = LocalDateTime.parse(date);
        return t;
    }

    public long getWorkerId () {
        Random random = new Random();
        return Math.abs(random.nextLong());
    }


    // Test case 1
    @Test
    public void testAgenciesUnequalTimesheets() {
        List<Timesheet> input = new ArrayList<>();
        // Agency A = 10 timesheets, Agency B = 1 timesheet
        for (int i = 0; i < 10; i++) {
            input.add(ts(1L, i + 1, getWorkerId(),"2025-01-01T00:00:0" + i));
        }
        input.add(ts(2L, 999,  getWorkerId(),"2025-01-01T00:00:11"));

        List<Timesheet> selected = service.selectTimesheets(input);

        assertEquals(11, selected.size());
        Set<Long> agencies = selected.stream().map(t -> t.agencyId).collect(Collectors.toSet());
        assertTrue(agencies.containsAll(Arrays.asList(1L, 2L)));
    }

    //Test Case 2
    @Test
    public void testAgenciesEqualTimesheets() {
        List<Timesheet> input = new ArrayList<>();
        // 3 agencies, each with 10 timesheets
        for (long agency = 1; agency <= 3; agency++) {
            for (int i = 0; i < 10; i++) {
                input.add(ts(agency, agency * 100 + i, getWorkerId(),"2025-01-01T00:00:0" + i));
            }
        }

        List<Timesheet> selected = service.selectTimesheets(input);

        // Batch size should be 30 (all available, since < 150)
        assertEquals(30, selected.size());

        // Each agency should have 10 timesheets selected
        Map<Long, Long> counts = selected.stream()
                .collect(Collectors.groupingBy(t -> t.agencyId, Collectors.counting()));

        assertEquals(10L, counts.get(1L));
        assertEquals(10L, counts.get(2L));
        assertEquals(10L, counts.get(3L));
    }

    // Test Case 3
    @Test
    public void testBatchSizeLimit() {
        List<Timesheet> input = new ArrayList<>();
        // 1 agency with 300 timesheets
        for (int i = 0; i < 300; i++) {
            input.add(ts(1L, i + 1, getWorkerId(),"2025-01-01T00:00:" + String.format("%02d", i % 60)));
        }

        List<Timesheet> selected = service.selectTimesheets(input);

        assertEquals(150, selected.size());
    }

    // Test Case 4
    @Test
    public void testDateOrderingWithinAgency() {
        List<Timesheet> input = new ArrayList<>();
        // 1 agency, out-of-order dates
        input.add(ts(1L, 1, getWorkerId(),"2025-01-02T00:00:00"));
        input.add(ts(1L, 2, getWorkerId(), "2025-01-01T00:00:00"));
        input.add(ts(1L, 3, getWorkerId(),"2025-01-03T00:00:00"));

        List<Timesheet> selected = service.selectTimesheets(input);

        // Checked to pick up in chronological order
        assertEquals(2L, selected.get(0).timesheetId);
        assertEquals(1L, selected.get(1).timesheetId);
        assertEquals(3L, selected.get(2).timesheetId);
    }

}
