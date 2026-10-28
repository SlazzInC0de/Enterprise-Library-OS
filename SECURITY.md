# Security Policy

## Supported versions

Only the latest commit on the `main` branch receives security fixes.

## Reporting a vulnerability

Please do not open a public issue for security problems.

Report it privately by either:

- using **Report a vulnerability** under this repository's **Security** tab, or
- emailing **vsuryansh080405@gmail.com** with the subject `[Enterprise Library OS] Security report`.

Include:

- what the issue is and where it lives (class, method, input)
- steps or a minimal input that reproduces it
- the impact you expect

## What to expect

- An acknowledgement within 7 days.
- A fix or a clear decision within 30 days for confirmed issues, depending on severity.
- Credit in the fix commit if you would like it.

## Scope

Enterprise Library OS is a single-user desktop app. It has no network service and no accounts, and all data stays in a local SQLite file. In scope:

- SQL injection through book titles, authors or search input (`dao/BookDAO.java`)
- crafted input that crashes the app or corrupts `library.db`
- unsafe handling of the `library.db.url` system property
- vulnerable dependencies in `pom.xml`

Out of scope: attacks that need write access to the user's machine or the database file itself, and reports from automated scanners without a working proof of concept.
