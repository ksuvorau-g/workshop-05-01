# US-3.2: Search Tasks

## Story Information

**Epic:** Advanced Features (Future)  
**Priority:** P3 (Low)  
**Size:** Medium  
**Sprint:** 3+

## User Story

**As a** user  
**I want to** search through my tasks  
**So that** I can quickly find specific items

## Acceptance Criteria

```gherkin
Scenario: Search by task description
  Given I have a task "Buy groceries for dinner"
  When I enter "groceries" in the search field
  Then the task "Buy groceries for dinner" appears in results
  And other tasks are hidden

Scenario: Case-insensitive search
  Given I have a task "Buy GROCERIES"
  When I search for "groceries"
  Then the task is found

Scenario: Real-time search
  Given I have multiple tasks
  When I type "buy" in the search field
  Then results update immediately as I type
  And matching tasks are shown

Scenario: Clear search
  Given I have entered a search term
  When I click the clear button
  Then the search field is cleared
  And all tasks are shown again

Scenario: No results
  Given I have 10 tasks
  When I search for "xyz123"
  Then I see a message "No tasks found"
  And the search field remains active
```

## Dependencies

US-1.2 (View TODO List)

## Technical Notes

- Debounce search input (300ms recommended)
- Highlight matching text in results
- Search across description, notes, categories
- Maintain performance with large lists

## Technical Tasks

- [ ] Add search input component
- [ ] Implement search/filter logic
- [ ] Add debouncing for real-time search
- [ ] Update list rendering for search results
- [ ] Add search highlighting
- [ ] Write unit tests for search
- [ ] Optimize search performance for large lists
