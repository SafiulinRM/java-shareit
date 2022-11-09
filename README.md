# java-shareit
Template repository for Shareit project.
# Проект ShareIt
Шеринг как экономика совместного использования набирает сейчас всё большую полярность. Если в 2014 году глобальный рынок шеринга оценивался всего в $15 млрд, то к 2025 может достигнуть $335 млрд.

Почему шеринг так популярен. Представьте, что на воскресной ярмарке вы купили несколько картин и хотите повесить их дома. Но вот незадача — для этого нужна дрель, а её у вас нет. Можно, конечно, пойти в магазин и купить, но в такой покупке мало смысла — после того, как вы повесите картины, дрель будет просто пылиться в шкафу. Можно пригласить мастера — но за его услуги придётся заплатить. И тут вы вспоминаете, что видели дрель у друга. Сама собой напрашивается идея — одолжить её.

Большая удача, что у вас оказался друг с дрелью и вы сразу вспомнили про него! А не то в поисках инструмента пришлось бы писать всем друзьям и знакомым. Или вернуться к первым двум вариантам — покупке дрели или найму мастера. Насколько было бы удобнее, если бы под рукой был сервис, где пользователи делятся вещами! 

Сервис обеспечивает пользователям, во-первых, возможность рассказывать, какими вещами они готовы поделиться, а во-вторых, находить нужную вещь и брать её в аренду на какое-то время.

Сервис не только позволяет бронировать вещь на определённые даты, но и закрывать к ней доступ на время бронирования от других желающих. На случай, если нужной вещи на сервисе нет, у пользователей должна быть возможность оставлять запросы. Вдруг древний граммофон, который странно даже предлагать к аренде, неожиданно понадобится для театральной постановки. По запросу можно будет добавлять новые вещи для шеринга.

# Детали приложения
Пользователь, который добавляет в приложение новую вещь, будет считаться ее владельцем. При добавлении вещи есть возможность указать её краткое название и добавить небольшое описание. К примеру, название может быть — «Дрель “Салют”», а описание — «Мощность 600 вт, работает ударный режим, так что бетон возьмёт». Также у вещи обязательно должен быть статус — доступна ли она для аренды. Статус должен проставлять владелец.
Для поиска вещей организован поиск. Чтобы воспользоваться нужной вещью, её требуется забронировать. Бронирование, или Booking — ещё одна важная сущность приложения. Бронируется вещь всегда на определённые даты. Владелец вещи обязательно должен подтвердить бронирование.
После того как вещь возвращена, у пользователя, который её арендовал, существует возможность оставить отзыв. В отзыве можно поблагодарить владельца вещи и подтвердить, что задача выполнена — дрель успешно справилась с бетоном, и картины повешены.
Ещё одна сущность, которая вам понадобится, — запрос вещи ItemRequest. Пользователь создаёт запрос, если нужная ему вещь не найдена при поиске. В запросе указывается, что именно он ищет. В ответ на запрос другие пользовали могут добавить нужную вещь.
