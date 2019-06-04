package gamemodelling.parchis.domain

import java.lang.IllegalArgumentException

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
    Toca modelar la casa. En este punto empieza lo divertido. Aquí hay multitud de opciones. Podrí modelar la casa
    como una clase, mas concretamente como una entidad DDD(su identidad viene definida por el color de la misma). Para
    cumplir esta regla también podría definirla como un atributo o un estado de Ficha (si está en la casa o no). Otra
    opción sería dejar el concepto de casa de manera implícita como un atributo que pertenezca a Partida o a Tablero y
    cuyo tipo sea List<Token>, representando cuantas fichas hay en la casa. También se me ocurre que podría ser un
    servicio de dominio en forma de política (patrón policy). Por ahora voy a representarlo como una clase
     */
    data class House(val color: Color, val remainingTokens: List<Token>)

    /*
    Ahora llego al juego, de esta regla puedo extraer que un juego tiene una casa de cada color con fichas.
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

object SecondIteration {

    //Clases de iteración anterior sin cambios:
    enum class Color { RED, BLUE, YELLOW, GREEN }
    data class Token(val color: Color)
    data class House(val color: Color, val remainingTokens: List<Token>)

    /*
    Pensandolo mas despacio, veo que el jugador todavía no hay que modelarlo, me basta con saber el número de jugadores. Aun así,
    el modelo anterior tiene un problema, la clase Game está pensada de manera que en tiempo de compilación controle las casas posibles,
    pero eso hace que el atributo color en House sea redundante, pues podría dar lugar a incoherencias en tiempo de ejecución.

    Para solucionar esto podría quitar el atributo color de House, pero como Token también tiene color, tendría que quitar el atributo
    color de Token, y ahí se me hace mas raro. Por ahora entonces voy a cambiar a modelar las casas como un mapa de color a casa
    y controlar en runtime que no haya incoherencias
     */

    class Game(houses: List<House>) {
        private val houses = houses.map { it.color to it }.toMap()

        init {
            if (houses.size !in 2..4) throw IllegalArgumentException("There must be 2 to 4 houses")
        }
    }

    /*
    Ahora ya si, con esto solucionado, paso a modelar la operación de comenzar partida. Como digo, voy a jugar al tetris de tipos,
    por lo que no voy a entrar en implementaciones, lo voy a definir como una interfaz. También me valdría un typealias de una función.
    Si sigo los patrones de DDD me cuadraría bastante que esto sea un servicio de dominio, porque no parece una operación de ninguna
    otra clase que haya definido hasta ahora.

    Para que la definición se entienda mejor, voy a modelar "número de jugadores" como un typealias de Int, lo dejo
    en un fichero fuera de la iteración, porque kotlin no permite typealias encadenados a objetos
     */
    interface StartGame {
        operator fun invoke(players: PlayerNumber): Game
    }

/*
Con esto creo que tengo mas o menos bien modelada la primera regla, paso a la segunda:

- Las fichas se mueven en sentido contrario a las agujas del reloj.
 */

    /*
    Con solo esa frase, ya se que existe una operación que permite mover las fichas y que tienen un sentido predeterminado.
    En este caso, el que se muevan en sentido horario o antihorario es un detalle visual, en realidad para implementar lo que nos
    interesa es saber que avanzan por el tablero. Creo que voy a empezar por modelar una Casilla.

    Las casillas tienen una identidad, nos interesa poder diferenciarlas, por lo que las trataré como entidades de DDD.
    Su identidad puede ser simplemente el número que tienen en el tablero. Esta identidad será un Int, pero por claridad usaré
    también un typealias. También podría usar un value object de DDD para representarla. Esto me daría como beneficio
    algo mas de seguridad en tiempo de compilación, pero también habría que hacer mapeos seguramente.

    (Estas aclaraciones en un caso real las haría el experto de dominio. Para el ejercicio estoy usando a internet
    como experto de dominio)
     */
    data class Box(val id: BoxId)

    /*
    Ahora que ya tengo modelada una casilla, me faltaría modelar la operación de mover la ficha. Pero para ello primero
    tengo que saber como se mueven. Una vez mas, después de consultar al experto de dominio me entero de que se mueven
    un número de casillas determinado por un dado. Aquí dudo entre modelar ya el dado o olvidarme de el por ahora y
    modelar esta operación como una función que recibe el número de casillas a mover, la ficha y devuelve la nueva representación
    de donde está la ficha. Siguiendo esa línea de pensamiento también me genera el problema de como representar la posición de la
    ficha...

    Por ahora modelaré el dado, que es algo mas sencillo. Para ello lo voy a modelar como un servicio de dominio. En realidad
    un dado no deja de ser una función que genera números aleatorios de manera acotada, no veo necesidad de tratarlo como
    una entidad o como una clase con mas de una instancia.

    Dicho esto, puedo modelarlo como una función de Unit a Int o ser mas específico. La ventaja de Int es que es mas simple,
    no tengo que crear clases extras, pero el que usa esta función no va a saber cuantos resultados posibles habría del lanzamiento.
    Podría irme al otro extremo y modelar el resultado como un enumerado de 6 valores, pero la complejidad que añade creo que no compensa
    en este caso. Podría optar por un punto intermedio (que es lo que voy a hacer), creando un value object que acote el resultado y varios factory methods.
     */
    class D6Face
    //Uso un class en vez de un data class para poder hacer el constructor privado
    private constructor(val face: Int) {
        //al no ser un data class pero si un value object tengo que implementar equals, hashcode, etc
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as D6Face

            if (face != other.face) return false

            return true
        }

        override fun hashCode(): Int = face

        override fun toString(): String = "D6Face(face=$face)"

        //Factory methods para crear todos los posibles valores
        companion object {
            val ONE = D6Face(1)
            val TWO = D6Face(2)
            val THREE = D6Face(3)
            val FOUR = D6Face(4)
            val FIVE = D6Face(5)
            val SIX = D6Face(6)
        }

    }

    interface Dice {
        fun `throw`(): D6Face
    }

    /*
    El modelo anterior tiene lo bueno de ambos mundos, es fácil de manejar (mas que un enumerado, pues el valor es un Int)
    pero sigue manteniendo acotado el número de valores posibles. También podría haber usado un enum con atributos, pero por lo
    general prefiero evitarlos.

    Otra opción era un sealed class con 6 objects heredando de ella y una función when para que te devuelva el valor numérico,
    pero añade complejidad.

    Una cosa que me gusta mucho de este modelo es que sería fácil generalizar la interfaz dado para soportar diferentes tipos de dados:

    interface Dice<DiceFaces> {
        fun `throw`(): DiceFaces
    }

    Con la interfaz anterior, definir un nuevo dado que se pueda lanzar y sea de 20 caras, sería trivial.
     */

    /* Hasta aquí la segunda iteración */
}