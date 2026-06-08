This short and simple Kata should be performed using Test Driven Development (TDD).

There is a series of books about software development that have been read by a lot of developers who want to improve their development skills. Let’s say an editor, in a gesture of immense generosity to mankind (and to increase sales as well), is willing to set up a pricing model where you can get discounts when you buy these books. The available books are :

Clean Code (Robert Martin, 2008)
The Clean Coder (Robert Martin, 2011)
Clean Architecture (Robert Martin, 2017)
Test Driven Development by Example (Kent Beck, 2003)
Working Effectively With Legacy Code (Michael C. Feathers, 2004)
Rules
The rules are described below :

One copy of the five books costs 50 EUR.

If, however, you buy two different books from the series, you get a 5% discount on those two books.
If you buy 3 different books, you get a 10% discount.
With 4 different books, you get a 20% discount.
If you go for the whole hog, and buy all 5, you get a huge 25% discount.
Note that if you buy, say, 4 books, of which 3 are different titles, you get a 10% discount on the 3 that form part of a set, but the 4th book still costs 50 EUR.
Developers seeking to deliver quality products are queueing up with shopping baskets overflowing with these books. Your mission is to write a piece of code to calculate the price of any conceivable shopping basket.

For example, how much does this basket of books cost?

2 copies of the “Clean Code” book
2 copies of the “Clean Coder” book
2 copies of the “Clean Architecture” book
1 copy of the “Test Driven Development by Example” book
1 copy of the “Working effectively with Legacy Code” book
Answer :

(4 * 50 EUR) - 20% [first book, second book, third book, fourth book]

(4 * 50 EUR) - 20% [first book, second book, third book, fifth book]

= 160 EUR + 160 EUR

= 320 EUR (knowledge is priceless but has a cost)

Useful link
Clean Code - TDD : https://cleancoders.com/episode/clean-code-episode-6-p1

IMPORTANT: Implement the requirements focusing on writing the best code you can produce.
# Book Store Pricing Engine (Kata Solution)

An enterprise-ready decoupled architectural layout applying SOLID design principles to solve the complex bundle optimization problem.

## Tech Stack
- **Language Level:** Java 17 (utilizing Record types, stream mapping pipelines, and switch patterns)
- **Dependency Management & Lifecycle Automation:** Maven 3.x
- **Testing Framework Suite:** JUnit 5.8.1 (Jupiter Engine)

## Architecture Profile & SOLID Principles Realization
The application eliminates monolithic dependencies by assigning singular, targeted profiles to separate layers:

1. **Single Responsibility (SRP):**
    - `CartValidator`: Dedicated exclusively to protecting state data boundaries.
    - `BookRepository`: Isolated layer wrapping raw item definitions.
    - `BookPriceCalculator`: Pure decoupled calculation service focused completely on optimization.
2. **Open/Closed (OCP):**
    - Discount rule modifications are completely driven via `DiscountPolicyRegistry`. No system code changes are needed to modify discounts.
3. **REST-API Contract Layer:**
    - Designed to run inside a standard enterprise resource loop via `CartPricingController`, accepting decoupled payloads and serving accurate standard JSON HTTP structures.

## Error Prevention Controls
The application explicitly validates inbound arrays to verify that item IDs correspond to mapped objects. Passing a missing configuration index value (such as ID `6` or `12`) will trigger an immediate, descriptive error response through the REST layer interface.

## Quickstart Compilation Execution