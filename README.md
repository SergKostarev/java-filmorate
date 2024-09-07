# java-filmorate
Template repository for Filmorate project.

# Project database description

![Database structure scheme](/ER_Diagram.svg)

## Query examples

### User

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
VALUES (1, 'example@example.com', 'Some_login', 'Some_name', '1990-01-01');
```

4. Update user by identifier (e.g. 1)

```sql
UPDATE User
SET email = 'anotherexample@example.com'
WHERE user_id = 1
```

### Film

1. Get all films

```sql
SELECT *
FROM Film
```

2. Get film by identifier (e.g. 1)

```sql
SELECT *
FROM Film
WHERE film_id = 1
```

3. Create film

```sql
INSERT INTO Film
VALUES (1, 'Some_login', 'Some_description', '1990-01-01', 60, 1, 1);
```

4. Update film by identifier (e.g. 1)

```sql
UPDATE Film
SET description = 'Another_description'
WHERE film_id = 1
```

### Friendship

1. Add friend (e.g. user with identifier 1 adds user with identifier 2)

```sql
INSERT INTO Friendship
VALUES (1, 2, FALSE);
```

2. Accept invitation (e.g. user with identifier 2 accepts invitation of user identifier 1)

```sql
UPDATE Friendship
SET is_confirmed = TRUE
WHERE user_id_1 = 1 AND user_id_2 = 2
```

3. Get friends of user by identifier (e.g. 1)

```sql
SELECT *
FROM User
WHERE user_id IN (
  SELECT user_id_2
  FROM Friendship
  WHERE user_id_1 = 1
  UNION
  SELECT user_id_1
  FROM Friendship
  WHERE user_id_2 = 1
)
```

4. Get common friends of users by identifiers (e.g. 1 && 2)

```sql
SELECT *
FROM User
WHERE user_id IN (
  SELECT user_id_2
  FROM Friendship
  WHERE user_id_1 = 2
  UNION
  SELECT user_id_1
  FROM Friendship
  WHERE user_id_2 = 2

  INTERSECT
  
  SELECT user_id_2
  FROM Friendship
  WHERE user_id_1 = 1
  UNION
  SELECT user_id_1
  FROM Friendship
  WHERE user_id_2 = 1
)
```

5. Delete friend (e.g. user with identifier 1 deletes user with identifier 2)

```sql
DELETE
FROM Friendship
WHERE user_id_1 = 1 AND user_id_2 = 2
```

### Like

1. Add like (e.g. user with identifier 1 likes film with identifier 1)

```sql
INSERT INTO Like
VALUES (1, 1)
```

2. Dislike (e.g. user with identifier 1 dislikes film with identifier 1)

```sql
DELETE
FROM Like
WHERE user_id = 1 AND film_id = 1
```

3. Get most popular (e.g. 10) movies

```sql
SELECT *
FROM Film
WHERE film_id IN (
  SELECT film_id
  FROM Like
  GROUP BY film_id
  ORDER BY COUNT(user_id) DESC
  LIMIT 10
)
```
