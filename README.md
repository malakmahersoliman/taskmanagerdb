# TaskManagerDB 

A Room-powered database demo showing how to model a mini **task manager** with users, projects, tasks, attachments, and cross-references.  
Covers schema design, DAO queries, type converters, relations, Flow vs Suspend, and performance profiling.

---

## 1. Schema & Entities

### Entities
- **User**
  - `id`, `name`, `email` (unique)
- **Project**
  - `id`, `title`, `ownerId (FK → User)`, `labels` (`List<String>` via converter), `createdAt` (`Date` via converter)
- **Task**
  - `id`, `description`, `projectId (FK → Project)`
- **Attachment**
  - `id`, `filePath`, `taskId (FK → Task)`
- **ProjectTaskCrossRef**
  - Composite key `(projectId, taskId)` for many-to-many
 

### Indices & Constraints
- Unique email for `User`
- Unique (title, ownerId) for `Project`
- Unique (description, projectId) for `Task`
- Unique (filePath, taskId) for `Attachment`
- Composite PK for `ProjectTaskCrossRef`

---

## 2. UML Diagram
<img width="1407" height="558" alt="schema" src="https://github.com/user-attachments/assets/480b0e0f-5c35-455f-ba90-0e79ee453350" />




## 2.2 Seeding (DBSeed)

We seed demo data idempotently:
- **Users**: Alice, Bob  
- **Projects**: Android App (Alice), Website (Bob)  
- **Tasks**: Set up Room, Write DAO, Landing page  
- **Attachments**: diagram.png, spec.pdf  
- **CrossRef**: Bob’s Website ↔ DAO task

---

## 2.3 Converters & Relations

- **Converters**  
  - `List<String> ↔ String` (comma-separated)  
  - `Date ↔ Long`  

- **Relations**  
  - `ProjectWithTasks`:  
    - `directTasks`: tasks owned by the project  
    - `sharedTasks`: via `ProjectTaskCrossRef`
   
## 2.4 Suspend vs Flow

- **Suspend once**: one-time snapshot  
- **Flow**: continuous re-emissions when DB changes
  
---

## 2.5 UI Layer (Jetpack Compose)

We created a minimal Compose screen (`ProjectScreen`) with `TaskViewModel`:

- Collects project + tasks via Flow
- Displays project title and task list
- Updates automatically when DB changes

---

## 2.6 Raw SQL & Performance

We compare `@Query` vs `@RawQuery` for:  
**Projects with more than N tasks.**

### Results (Pixel 5, 100 iterations)

| Query type | 100× time (ns) |
|------------|----------------|
| @Query     | 56,781,750 ns  |
| @RawQuery  | 25,560,500 ns  |

Notes: times vary by run; `@RawQuery` happened to be faster here.


