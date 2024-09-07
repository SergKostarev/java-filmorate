# java-filmorate
Template repository for Filmorate project.

# Project database description

![Database structure scheme](/QuickDBD-Free%20Diagram%20(2).svg)

## Query examples

1. Get all users

```sql
SELECT *
FROM user
```

2. Get an user by identifier (for example, 1)

```sql
SELECT *
FROM user
WHERE user.id = 1
```


