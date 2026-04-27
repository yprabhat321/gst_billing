# Dependency Direction Rules

## Scope
Simple dependency rules for the backend package structure.

## Allowed Dependencies
- `api` can depend on `application` and DTO classes
- `application` can depend on `domain` and `infrastructure`
- `infrastructure` can depend on `domain`
- `domain` should not depend on Spring, web, or persistence packages

## Not Allowed (for this project stage)
- unnecessary extra adapter layers before they are needed by real features

## Boundary Notes
- API DTOs must not be used as domain entities
- Domain objects must not import Spring framework classes
- Keep domain logic pure without Spring annotations

## Conventions
- Package naming follows `com.amanna.billingmanagement.<layer>`
- Constructor injection is preferred for API and adapter classes
- Domain layer is immutable first and invariant driven
- Keep flow clear: controller -> service -> domain -> persistence
- Keep exception handling consistent and simple

