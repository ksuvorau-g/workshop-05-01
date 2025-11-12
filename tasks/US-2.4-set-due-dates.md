# US-2.4: Set Due Dates

## Story Information

**Epic:** Enhanced Task Management  
**Priority:** P2 (Medium)  
**Size:** Medium  
**Sprint:** 2-3

## User Story

**As a** user  
**I want to** set due dates for tasks  
**So that** I can track deadlines and prioritize time-sensitive items

## Acceptance Criteria

```gherkin
Scenario: Add due date to new task
  Given I am creating a new task
  When I select a due date of "2025-11-15"
  And I add the task
  Then the task is created with the due date
  And the due date "Nov 15, 2025" is displayed with the task

Scenario: Add due date to existing task
  Given I have a task without a due date
  When I click the date icon
  And I select "2025-11-15"
  Then the due date is added to the task
  And the due date is displayed

Scenario: Highlight overdue tasks
  Given I have a task with due date "2025-11-10"
  And today is "2025-11-11"
  When I view my task list
  Then the overdue task is highlighted in red
  And an "overdue" label is shown

Scenario: Clear due date
  Given I have a task with a due date
  When I click the clear date button
  Then the due date is removed
  And no date is displayed

Scenario: Sort by due date
  Given I have tasks with various due dates
  When I click "Sort by Due Date"
  Then tasks with due dates appear first
  And they are sorted by date (soonest first)
  And tasks without due dates appear last
```

## Dependencies

US-1.1 (Create TODO Item)

## Technical Notes

- Use native date picker or library (date-fns recommended)
- Handle timezone considerations
- Calculate overdue status on render
- Update data model to include dueDate field
- Format dates consistently (locale-aware)

## Technical Tasks

- [ ] Add due date field to task data model
- [ ] Integrate date picker component
- [ ] Implement due date display
- [ ] Add overdue highlighting logic
- [ ] Implement sorting by due date
- [ ] Update storage schema
- [ ] Write unit tests for due date feature
- [ ] Test timezone handling
