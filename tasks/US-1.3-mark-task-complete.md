# US-1.3: Mark Task as Complete

## Story Information

**Epic:** Task Management Core (MVP)  
**Priority:** P0 (Critical)  
**Size:** Small  
**Sprint:** 1

## User Story

**As a** user  
**I want to** mark tasks as complete  
**So that** I can track my progress and distinguish finished tasks

## Acceptance Criteria

```gherkin
Scenario: Mark task as complete
  Given I have an incomplete task "Buy groceries"
  When I click the checkbox next to the task
  Then the task is marked as complete
  And the task text has a strikethrough style
  And a checkmark appears in the checkbox

Scenario: Unmark completed task
  Given I have a completed task "Buy groceries"
  When I click the checkbox next to the task
  Then the task is marked as incomplete
  And the strikethrough style is removed
  And the checkbox is unchecked

Scenario: Persist completion status
  Given I have marked a task as complete
  When I refresh the page
  Then the task remains marked as complete

Scenario: Visual distinction
  Given I have both complete and incomplete tasks
  When I view my task list
  Then completed tasks are visually distinct from incomplete tasks
  And I can easily tell them apart
```

## Dependencies

US-1.2 (View TODO List)

## Technical Notes

- Accessible checkbox (ARIA labels, keyboard navigation)
- Animation for completion toggle (optional but recommended)
- Consider showing completion timestamp

## Technical Tasks

- [ ] Add checkbox/button UI element
- [ ] Implement toggle completion function
- [ ] Update task state in storage
- [ ] Add visual styling for completed tasks
- [ ] Implement persistence logic
- [ ] Write unit tests for completion toggle
- [ ] Add accessibility features (ARIA labels)
