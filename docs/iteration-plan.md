# Iteration 2 Plan and Risk List (D9)

## Top-5 risks
| # | Risk | Likelihood / impact | Mitigation |
|---|---|---|---|
| 1 | I2 rules (minimum basket, opening hours) get added to the UI or to `OrderingService` and the domain loses its rule homes. | Medium / High | Add them in `Order.place` / `Cart` with new extensions in the use cases first; keep the "one rule, one class" table up to date; review PRs against it. |
| 2 | Order status changes (accepted, delivered...) break the snapshot design or the `OrderStatus` enum. | Medium / High | Keep `Order` fields immutable except `status`; add transitions as one method with a state check; write tests per transition before coding. |
| 3 | Restaurant owner and courier introduce new actors and the console UI turns into a 600-line class. | High / Medium | One UI class per actor (or per command group) behind the same `ConsoleFormatter`; keep `OrderingService` per actor (`OwnerService`, `CourierService`). |
| 4 | Team members have uneven contributions / merge conflicts on shared classes. | Medium / Medium | Small PRs, one owner per class, review required, board with WIP limit; contribution table updated weekly. |
| 5 | Documents and code drift apart (glossary, domain model, contracts). | High / Medium | Definition of done: change in code ⇒ same PR updates the matching doc; check glossary words with `grep` before demo. |

## Plan for Iteration 2 (first draft)
| Week | Work | Owner |
|---|---|---|
| 8 | Update vision, glossary and use cases for restaurant owner (menu management, accept order) and the new rules (minimum basket, opening hours). | to assign |
| 9 | Domain: `Order` status transitions, `OpeningHours`, minimum-basket check in `Order.place`; tests first. | to assign |
| 10 | Application and UI: owner commands, opening-hours messages, orders per restaurant; JSON gets `openingHours`. | to assign |
| 11 | Integration, README, sample run, report, tag iteration-2. | all |

## Retrospective of Iteration 1 (to be completed by the team)
- What went well:
- What to improve:
- Action items:

