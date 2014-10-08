#ToDoList V0.1


Esta versión a introducido un fuerte cambio en la app, pasando de un sencillo formulario a una API REST y añadiendo lo siguiente:
* Las tasks pertenecen (Obligatoriamente) a un usuario
>* Las tasks creadas en V0.0 siguen existiendo pero las funcionalidades sobre, y persistencia de, las mismas no se asegura en futuras versiones (lo mismo para las tasks sin usuario que se creen de ahora en adelante)
* Las tasks poseen una fecha (Opcionalmente) que marca el fin de la misma (las tasks fuera de fecha no son eliminadas por el sistema)
>* Este campo sigue el formato 'dd/MM/yyyy', Ej: 25/11/1993, 01/01/2014, etc.
>* Se han añadido filtros de tasks sobre sus fechas (detalladas más adelante)



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


###Task

####Patrón

    {
        "id":id_del_task,
        "label":descripcion_task,
        "owner":{
            "nick":nick_del_propietario_del_task
        },
        "deadend":fecha_fin_task
    }

####Ejemplo

    {
        "id":777,
        "label":"Hacer documentación V0.1",
        "owner":{
            "nick":"edgar"
        },
        "deadend":"15/10/2014"
    }

####Ejemplo sin deadend

    {
        "id":777,
        "label":"Hacer documentación V0.1",
        "owner":{
            "nick":"edgar"
        },
        "deadend":null
    }



##Funciones Activas


###Tasks en general


####GET /tasks/:id

Devuelve la task con el id dado en la URL


####DELETE /tasks/:id

Elimina la task con el id dado en la URL


###Tasks de usuarios


####GET /:user/tasks

Devuelve todas las tasks del usuario dado en la URL


####POST /:user/tasks

Crea una task del usuario dado en la URL con los datos dados en el cuerpo de la petición (Devuelve un Json con la task creada):
* 'label' - String: campo obligatorio, representa el contenido de la task
* 'deadend' - Date('dd/MM/yyyy'): campo opcional, representa la fecha de finalización de la task


###Filtros de deadend
 

####GET /:user/tasks/ends_after?endsAfter=<fecha>

Devuelve las tareas del usuario que finalizan despues de la fecha dada en la URL*
 

####GET /:user/tasks/ends_between?rangeBegin=<fecha>&rangeEnd=<fecha>

Devuelve las tareas del usuario que finalizan en el rango de fechas [rangeBegin,rangeEnd] dadas en la URL*
 

####GET /:user/tasks/no_deadend

Devuelve las tareas del usuario que no poseen deadend


*Las fechas dadas por URL deben tener el formato 'dd-MM-yyyy'



##Funciones Obsoletas


####GET /tasks

Devuelve todas las tasks sin usuario


####POST /tasks

Crea una task sin usuario (tiene el mismo formato de parametros que POST /:user/tasks)


####/ui[/...]

La interfaz gráfica (e hijas) siguen funcionando y se les ha incluido el campo 'deadend' pero no permite especificar usuario (y carece de filtros)



##Funciones Eliminadas


####Ninguna

Pero se prevee que las funciones actualmente marcadas como obsoletas sean eliminadas en futuras versiones