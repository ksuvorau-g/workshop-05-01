# US-2.1: Edit TODO Item

## Story Information

**Epic:** Enhanced Task Management  
**Priority:** P1 (High)  
**Size:** Medium  
**Sprint:** 2

## User Story

**As a** user  
**I want to** edit existing TODO items  
**So that** I can correct mistakes or update task descriptions

## Acceptance Criteria

```gherkin
Scenario: Edit task inline
  Given I have a task "Buy grocries" (typo)
  When I double-click on the task
  Then the task enters edit mode with an input field
  And the current text "Buy grocries" is selected

Scenario: Save edited task
  Given I am editing a task
  When I change the text to "Buy groceries"
  And I press Enter
  Then the task is updated with the new text
  And the task exits edit mode

Scenario: Cancel editing
  Given I am editing a task
  When I press Escape
  Then the changes are discarded
  And the task shows the original text
  And the task exits edit mode

Scenario: Prevent saving empty task
  Given I am editing a task
  When I clear all the text
  And I try to save
  Then I see a validation message
  And the task is not saved
  And I remain in edit mode
```

## Dependencies

US-1.2 (View TODO List)

## Technical Notes

- Inline editing preferred over modal
- Auto-focus and select text when entering edit mode
- ESC to cancel, Enter to save
- Click outside to save (optional)

## Technical Tasks

- [ ] Add edit mode to task component
- [ ] Implement inline editing UI
- [ ] Add save/cancel functionality
- [ ] Update task in storage
- [ ] Handle keyboard shortcuts (Enter/Escape)
- [ ] Write unit tests for edit flow
- [ ] Test edge cases (editing multiple items, rapid edits)
