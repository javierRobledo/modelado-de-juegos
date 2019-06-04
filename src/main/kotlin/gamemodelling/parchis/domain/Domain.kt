package gamemodelling.parchis.domain

/*
Primera iteración:
- De lo que recuerdo del parchís y sabiendo elementos comunes en juegos de mesa, se que habrá elementos importantes como
Partida, Jugador, Ficha, Tablero o Casilla, pero para modelar iré analizando cada regla e iterando sobre ellas.

- Para las reglas, usaré: http://blog.nnatali.com/2009/04/27/reglas-del-parchis-comete-1-y-cuenta-20/

- Voy a crear un objeto a modo de namespace para cada iteración, para poder separar claramente el razonamiento que voy siguiendo

- Voy a modelar utilizando "tetris de tipos"
*/

object FirstIteration {

/*
– Al comenzar el juego todas las fichas están en la casa de su color.

De esta regla extraemos varias cosas, primero, hay 4 términos importantes, tenemos por una parte "Juego" y por otra
fichas, casa y color, que da la sensación que están relacionadas. También habla de "al comenzar", lo que podríamos
entender como un estado o como una operación
 */

    /*
    Modelamos primero color, podríamos optar por un string o un campo libre,
    pero prefiero cerrar lo máximo posible los valores que hay, por lo que uso un enum
     */
    enum class Color { RED, BLUE, YELLOW, GREEN }

    /*
    A la hora de modelar la ficha, todavía no tengo muy claro si la voy a tratar como una entidad o como un valor.
    Va a depender mucho de los atributos que use. En principio, las fichas son indistinguibles entre si, su identidad
    viene definida por la casilla en la que están, pero aún no hemos hablado de casillas y no se como modelaré estas.
    Por ahora voy a modelar la ficha como una clase con un atributo que es el color
     */
    data class Token(val color: Color)

    /*
    Toca modelar la casa. En este punto empieza lo divertido. Aquí hay multitud de opciones. Podríamos modelar la casa
    como una clase, mas concretamente como una entidad DDD(su identidad viene definida por el color de la misma). Para
    cumplir esta regla también podríamos definirla como un atributo o un estado de Ficha (si está en la casa o no). Otra
    opción sería dejar el concepto de casa de manera implícita como un atributo que pertenezca a Partida o a Tablero y
    cuyo tipo sea List<Token>, representando cuantas fichas hay en la casa. Por ahora voy a representarlo como una clase
     */
    data class House(val color: Color, val remainingTokens: List<Token>)

    /*
    Ahora llegamos al juego, de esta regla podemos extraer que un juego tiene una casa de cada color con fichas.
    Para modelar esta restricción podría usar una lista, un mapa de color a casa o 4 atributos, uno distinto para cada casa,
    de manera que le de la identidad de manera implícita y cierre en tiempo de compilación estados no representables.
    A cambio, la aparición de nuevos colores me obligaría a añadir nuevos atributos. Por ahora, la opción que mas me llama
    es la de 4 atributos, uno por cada color.
     */
    class Game(val redHouse: House,
               val yellowHouse: House,
               val greenHouse: House,
               val blueHouse: House)

    /*
    Por último, queda modelar la operación de comenzar partida. Llegado a este punto me doy cuenta de que no he tenido en cuenta
    que el número de jugadores podría no ser 4, asique me va a tocar cambiar lo que tenía modelado hasta ahora. Continuo en
    la segunda iteración
     */
}