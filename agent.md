# Development Guidelines

## Language

All user-facing text in the application must be written in English. This includes labels, buttons, menus, dialogs, placeholders, alerts, validation messages, logs intended for users, and any visible content rendered in the UI.

## Implementation Principles

Functions and components must be designed to be as useful, reusable, and robust as possible.

Every implementation should assume that the application may grow significantly over time, so code must be prepared for extension without requiring fragile rewrites.

When adding or updating functions:

- Prefer designs that can support new use cases with minimal changes.
- Keep logic centralized when it improves reuse and maintainability.
- Avoid tightly coupled solutions that only solve one immediate scenario.
- Use clear inputs and outputs so the same function can serve future features.
- Make the internal behavior predictable and safe, including null handling, state handling, and error handling when appropriate.
- Favor scalable structures that help new events, views, and workflows plug into the existing system cleanly.
- Implement with readability and long-term maintainability in mind, not only short-term speed.

## Architectural Intent

Shared functions should be built so future functionality can either reuse them directly or extend them internally with minimal friction.

The default mindset for new development should be: reusable first, scalable by design, and stable under future growth.
