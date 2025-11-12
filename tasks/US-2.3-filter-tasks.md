# US-2.3: Filter Tasks

## Story Information

**Epic:** Enhanced Task Management  
**Priority:** P2 (Medium)  
**Size:** Small  
**Sprint:** 2

## User Story

**As a** user  
**I want to** filter tasks by status  
**So that** I can focus on active or completed tasks

## Acceptance Criteria

```gherkin
Scenario: View all tasks
  Given I have 3 active and 2 completed tasks
  When I select the "All" filter
  Then I see all 5 tasks

Scenario: View active tasks only
  Given I have 3 active and 2 completed tasks
  When I select the "Active" filter
  Then I see only the 3 active tasks
  And completed tasks are hidden

Scenario: View completed tasks only
  Given I have 3 active and 2 completed tasks
  When I select the "Completed" filter
  Then I see only the 2 completed tasks
  And active tasks are hidden

Scenario: Task count display
  Given I am on any filter view
  When I look at the filter buttons
  Then I see the count next to each filter
  And the counts are accurate (e.g., "Active (3)", "Completed (2)")

Scenario: Persist filter selection
  Given I have selected the "Active" filter
  When I refresh the page
  Then the "Active" filter is still selected
```

## Dependencies

US-1.3 (Mark Task as Complete)

## Technical Notes

- Filter buttons: All, Active, Completed
- Highlight active filter
- Update URL or state to persist filter
- Keyboard shortcuts optional (1, 2, 3 keys)

## Technical Tasks

- [ ] Create filter UI component (buttons or dropdown)
- [ ] Implement filter logic
- [ ] Update list rendering to respect filter
- [ ] Persist filter selection in storage
- [ ] Add visual indication of active filter
- [ ] Write unit tests for filter functionality
- [ ] Test with various data combinations
