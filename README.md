# Referee

A DATC-compliant* Diplomacy adjudicator, written in base Java.

At present, the program (i.e. its only entry point) reads DATC test cases from disk, and compares them to  the adjudicator's results. 

These test cases include:
* Handling illegal orders
* Handling invalid orders
* Basic movement - i.e. 'moves, holds, supports, & convoys'
* Advanced tactics - e.g. cyclical movement, head-to-head battles, beleaguered garrisons, convoy swaps, etc.
* "Simple" convoy paradoxes
* Multi-layer convoy paradoxes

\*Several DATC cases are incompatible with the program - see: *Limitations and Idiosyncrasies* below, and `misc/testcase_alterations`.

---

## Top-level classes

- `Judge.java` — resolves a set of orders
- `Referee.java` (extends Judge) — runs multiple shuffled adjudications and selects a final result when raw results differ
- `SzykmanReferee.java` (extends Referee) - special Referee used for convoy paradoxes
- `TestCaseManager.java` — loads and runs DATC test cases

---

## Tests

Run `TestCaseManager`.

The test cases are read from `src/resources/testgames/*` and `/src/resources/testgames_solutions/*`

The program prints each test result and gives a final DATC-compliance score.

---

## Limitations & Idiosyncrasies

1. *Referee* allows convoy kidnapping.

It is fun!
Some adjudicators (like <a href="https://www.backstabbr.com/">Backstabbr</a>) allow it, and some do not.


2. *Referee* does not natively support a `via convoy` flag.

The existence of a convoy operation is an implied result of the convoying fleet 'succeeding', the move succeeding, and the move itself existing.

3. `6.F.29. TEST CASE, SIXTH ORDER BUTTERFLY EFFECT` is not yet resolved correctly.

The program detects the layer-6 convoy dependency as a paradox and applies Szykman-style convoy handling. However, F29 has one valid normal resolution, so that fallback should not be used.
i.e. *Referee* triggers the Szykman rule here, while DATC does not.

This defect is limited in scope to situations where convoy paradoxes exist within the provided orders, which is in itself an exceptionally rare occurrence.

---