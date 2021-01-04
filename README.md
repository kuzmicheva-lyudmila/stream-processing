Stream processing
Реализовать сервис заказа. Сервис биллинга. Сервис нотификаций.

При создании пользователя, необходимо создавать аккаунт в сервисе биллинга. В сервисе биллинга должна быть возможность положить деньги на аккаунт и снять деньги.

Сервис нотификаций позволяет отправить сообщение на email. И позволяет получить список сообщений по методу API.

Пользователь может создать заказ. У заказа есть параметр - цена заказа.
Заказ происходит в 2 этапа:
1) сначала снимаем деньги с пользователя с помощью сервиса биллинга
2) отсылаем пользователю сообщение на почту с результатами оформления заказа. Если биллинг подтвердил платеж, должно отослаться письмо счастья. Если нет, то письмо горя.

Упрощаем и считаем, что ничего плохого с сервисами происходить не может (они не могут падать и т.д.). Сервис нотификаций на самом деле не отправляет, а просто сохраняет в БД.

ТЕОРЕТИЧЕСКАЯ ЧАСТЬ (5 баллов):
0) Спроектировать взаимодействие сервисов при создании заказов. Предоставить варианты взаимодействий в следующих стилях в виде sequence диаграммы с описанием API на IDL:
- только HTTP взаимодействие
- событийное взаимодействие с использование брокера сообщений для нотификаций (уведомлений)
- Event Collaboration cтиль взаимодействия с использованием брокера сообщений
- вариант, который вам кажется наиболее адекватным для решения данной задачи. Если он совпадает одним из вариантов выше - просто отметить это.


ПРАКТИЧЕСКАЯ ЧАСТЬ (5 баллов):
Выбрать один из вариантов и реализовать его.
На выходе должны быть
0) описание архитектурного решения и схема взаимодействия сервисов (в виде картинки)
1) команда установки приложения (из helm-а или из манифестов). Обязательно указать в каком namespace нужно устанавливать.
2) тесты постмана, которые прогоняют сценарий:
1. Создать пользователя. Должен создаться аккаунт в биллинге.
2. Положить деньги на счет пользователя через сервис биллинга.
3. Сделать заказ, на который хватает денег.
4. Посмотреть деньги на счету пользователя и убедиться, что их сняли.
5. Посмотреть в сервисе нотификаций отправленные сообщения и убедиться, что сообщение отправилось
6. Сделать заказ, на который не хватает денег.
7. Посмотреть деньги на счету пользователя и убедиться, что их количество не поменялось.
8. Посмотреть в сервисе нотификаций отправленные сообщения и убедиться, что сообщение отправилось.

В тестах обязательно
- наличие {{baseUrl}} для урла
- использование домена arch.homework в качестве initial значения {{baseUrl}}
- отображение данных запроса и данных ответа при запуске из командной строки с помощью newman.
