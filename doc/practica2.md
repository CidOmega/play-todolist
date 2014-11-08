#ToDoList V0.2


En esta nueva versión se han añadido las categorías de usuario:

* Una categoría pertenece a un usaurio
* Una categoría posee un nombre unico (dentro de las categorias de un mismo usuario)
* Una categoria puede contener varias tareas
* Una tarea puede estar en varias categorias


##Formatos JSON


###User

####Patrón

    {
        "nick":nick_del_usuario
    }

####Ejemplo

    {
        "nick":"edgar"
    }


###Category

####Patrón

    {
        "name":nombre_categoria
    }

####Ejemplo

    {
        "name":"todo"
    }


###Task

####Patrón

    {
        "id":id_del_task,
        "label":descripcion_task,
        "owner":{
            "nick":nick_del_propietario_del_task
        },
        "deadend":fecha_fin_task
        "categories":[
            {"name": nombre_categoria}, ...
        ]
    }

####Ejemplo con deadend, sin categorias

    {
        "id":777,
        "label":"Hacer documentación V0.1",
        "owner":{
            "nick":"edgar"
        },
        "deadend":"15/10/2014"
        "categories":[]
    }

####Ejemplo sin deadend, con categorias

    {
        "id":777,
        "label":"Hacer documentación V0.1",
        "owner":{
            "nick":"edgar"
        },
        "deadend":null
        "categories":[
            {"name": "todo"},
            {"name": "importante"}
        ]
    }



##Funciones Activas


###Tasks en general


####GET /tasks/[id]

Sin cambios de funcionalidad con la v0.1
El json devuelto añade las categorias


####DELETE /tasks/[id]

Sin cambios de funcionalidad con la v0.1


###Datos Usuarios


####GET /[user]

Sin cambios de funcionalidad con la v0.1


####GET /[user]/tasks

Sin cambios de funcionalidad con la v0.1
El json devuelto añade las categorias


###Filtros de deadend
 

####GET /[user]/tasks/ends_after?endsAfter=[fecha]

Sin cambios de funcionalidad con la v0.1
El json devuelto añade las categorias


####GET /[user]/tasks/ends_at?endsAt=[fecha]

Sin cambios de funcionalidad con la v0.1
El json devuelto añade las categorias
 

####GET /[user]/tasks/ends_before?endsBefore=[fecha]

Sin cambios de funcionalidad con la v0.1
El json devuelto añade las categorias
 

####GET /[user]/tasks/ends_between?rangeBegin=[fecha]&rangeEnd=[fecha]

Sin cambios de funcionalidad con la v0.1
El json devuelto añade las categorias
 

####GET /[user]/tasks/ends_today

Sin cambios de funcionalidad con la v0.1
El json devuelto añade las categorias
 

####GET /[user]/tasks/outdate

Sin cambios de funcionalidad con la v0.1
El json devuelto añade las categorias
 

####GET /[user]/tasks/no_deadend

Sin cambios de funcionalidad con la v0.1
El json devuelto añade las categorias


###Categorias

####GET /[user]/categories

Devuelve las categorías pertenecientes al usuario dado (puede ser un array vacío)

####GET /[user]/categories/[category]

Devuelve la categoría perteneciente al usuario dado (de existir dicha categoría)

####GET /[user]/categories/[category]/tasks

Devuelve las tareas de la categoría dada perteneciente al usuario dado (puede ser un array vacío (o un 404 si el user o la categoria no existen))


###Acciones sobre tareas de usuarios


####POST /[user]/tasks

Sin cambios de funcionalidad con la v0.1
El json devuelto añade las categorias (que por ahora siempre estara vacio en la nueva tarea)
 

####DELETE /[user]/tasks/outdate

Sin cambios de funcionalidad con la v0.1


###Acciones sobre categorias de usuarios

####POST /[user]/categories

Crea una categoría asociada al usuario dado (devuelve el json de la categoría creada):
* 'name' - String: campo obligatorio, el nombre de la categoria a crear

####POST /[user]/categories/[category]/tasks

Asocia una tarea a la categoría del usuario dados (devuelve el json de la tarea actualizada si todo es correcto, falla si la tarea ya era de la categoria):
* 'id' - Int: campo obligatorio, el id de la tarea a asociar

####DELETE /[user]/categories/[category]/tasks

Desasocia una tarea a la categoría del usuario dados (devuelve el json de la tarea actualizada si todo es correcto, falla si la tarea no era de la categoria):
* 'id' - Int: campo obligatorio, el id de la tarea a asociar


##Funciones Obsoletas


####GET /tasks

Sin cambios de funcionalidad con la v0.1
El json devuelto añade las categorias (array vacio)


####POST /tasks

Sin cambios de funcionalidad con la v0.1
El json devuelto añade las categorias (array vacio)


####/ui[/...]

Sin cambios con la v0.1



##Funciones Eliminadas


####Ninguna

Las funciones obsoletas siguen activas con prevision a ser borradas



##Detalles de implementación


###Controllers


####Global

Se ha añadido el formulario para editar tareas (no ignora el id, y en los campos pone opciones por defecto)
> No se ha podido refactorizar en la v0.2 en cajas de herramientas


####Tasks

Sin cambios de funcionalidad con la v0.1
Las tasks se devuelven con el nuevo formato Json (incluyendo las categorias)


####Ui

Sin cambios con la v0.1


####Users

Sin cambios de funcionalidad con la v0.1
Las tasks se devuelven con el nuevo formato Json (incluyendo las categorias)


####UsersCategories

Contiene las funciones referentes a las categorias

* Devolver las categorias de un usuario
* Devolver los datos de una categoría
* Devolver las tareas de una categoría
* Enlazar una tarea con una categoría
* Desenlazar una tarea de una categoría


###Models

Se ha añadido el objeto de acceso y la clase de transmisión de datos referente a las categorias (fichero Category.scala)


###Views

Sin cambios con la v0.1 (sigue en vista a ser borrado)