# java-filmorate
Template repository for Filmorate project.

# Project database description

![Database structure scheme](/QuickDBD-Free%20Diagram%20(2).svg)

## Query examples

1. Get all users

```sql
SELECT *
FROM User
```

2. Get user by identifier (e.g. 1)

```sql
SELECT *
FROM User
WHERE user_id = 1
```

3. Create user

```sql
INSERT INTO User
VALUES (1, example@example.com, Some_login, Some_name, '1990-01-01');
```

4. Update user by identifier (e.g. 1)

```sql
UPDATE User
SET email = 'example@example.com'
WHERE user_id = 1
```



