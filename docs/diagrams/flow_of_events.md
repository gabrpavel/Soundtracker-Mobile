# Поток событий

## Вход и регистрация в приложении
**Актеры:** Пользователь, Система

**Цель:** Войти в существующий аккаунт или создать новый

**Предусловия:** Пользователь установил приложение и открыл его

### Основной поток:
1. Пользователь открывает приложение.
2. Система отображает форму входа.
3. Пользователь выбирает, есть ли у него аккаунт:
    - Если аккаунт есть, пользователь вводит логин и пароль.
    - Если аккаунта нет, пользователь переходит к форме регистрации.
4. Система проверяет корректность введенных данных:
    - Если данные введены правильно, система предоставляет доступ к аккаунту.
5. Пользователь успешно входит в аккаунт.

### Альтернативный поток (Регистрация):
1. На шаге 3, если у пользователя нет аккаунта, он переходит к форме регистрации.
2. Пользователь вводит необходимые данные (например, логин, пароль, адрес электронной почты).
3. Пользователь нажимает кнопку "Зарегистрироваться".
4. Система проверяет корректность введенных данных:
    - Если данные корректны, система регистрирует пользователя.
    - Если данные некорректны, система уведомляет пользователя об ошибке и предлагает исправить данные.
5. После успешной регистрации пользователь автоматически входит в аккаунт.

### Альтернативный поток (Ошибка входа):
1. На шаге 4, если данные введены некорректно (например, неверный пароль):
    - Система уведомляет пользователя о неверных данных и предлагает повторить попытку ввода.

## Поиск фильма и написание отзыва в приложении "Soundtracker"
**Актеры:** Пользователь, Система

**Цель:** Найти фильм и написать отзыв

**Предусловия:** Пользователь установил приложение и открыл его

### Основной поток:
1. Пользователь открывает приложение.
2. Пользователь переходит на главную страницу.
3. Пользователь вводит название фильма.
4. Система проверяет, есть ли фильмы, соответствующие введенному названию.
5. Если подходящие фильмы есть, система выводит список.
6. Пользователь выбирает фильм из предложенного списка и переходит к странице фильма.
7. Система предоставляет пользователю возможность написания отзыва к фильму.

### Альтернативный поток:
1. На шаге 5, если фильмов, соответствующих введенному названию, не найдено, пользователю предлагается ввести другое название и повторить попытку.

## Редактирование профиля пользователя
**Актеры:** Пользователь, Система

**Цель:** Изменить данные профиля пользователя

**Предусловия:** Пользователь установил приложение и открыл его

### Основной поток:
1. Пользователь открывает приложение.
2. Пользователь переходит на страницу своего профиля.
3. Пользователь выбирает опцию "Редактировать профиль".
4. Система отображает форму редактирования профиля.
5. Пользователь изменяет необходимые данные (например, имя, фамилию, адрес электронной почты).
6. Пользователь нажимает кнопку "Сохранить изменения".

### Альтернативный поток:
1. На шаге 5, если пользователь ввел некорректные данные:
    - Система уведомляет пользователя об ошибке и предлагает исправить данные.