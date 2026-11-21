package pagecode.apps.reporte;
/*
 * @(#)CifrasEnLetras.java
 */
 
//package net.proinf;
 
/**
 * CifrasEnLetras sirve para expresar una serie de cifras en letras.
 * A modo de ejemplo convierte <q>22</q> en <q>veintidós</q>.
 * Puede convertir un número entre una y ciento veintiséis cifras como máximo.
 *
 * <dl>
 * <dt>Ejemplos de uso:</dt>
 * <dd><code>CifrasEnLetras.convertirEurosEnLetras(22.34)</code> &rarr; <samp>"veintidós euros con treinta y cuatro céntimos"</samp></dd>
 * <dd><code>CifrasEnLetras.convertirNumeroEnLetras("35,67")</code> &rarr; <samp>"treinta y cinco con sesenta y siete"</samp></dd>
 *
 * <dt>Enlaces de referencia:</dt>
 * <dd><a href="http://es.wikipedia.org/wiki/Anexo:Lista_de_n%C3%BAmeros">Lista de números en la Wikipedia</a></dd>
 * <dd><a href="http://es.encarta.msn.com/encyclopedia_761577100/Signos_matem%C3%A1ticos.html">Signos matemáticos en Encarta</a></dd>
 * <dd><a href="http://es.wikipedia.org/wiki/Nombres_de_los_n%C3%BAmeros_en_espa%C3%B1ol">Nombres de los números en español</a></dd>
 *
 * <dt>Tareas pendientes:</dt>
 * <dd>Función ConvertirAñosEnLetras: "cuatro años", "un lustro", "una década", "dos décadas", "un siglo", "un milenio"</dd>
 * <dd>Función ConvertirOrdinalesEnLetras: "primero", "segundo", etc.</dd>
 * <dd>Función ConvertirCifrasEnRomano y convertirRomanoEnCifras</dd>
 * <dd>Función ConvertirFechasEnLetras: "21-XI-2007" &rarr; "veintiuno de noviembre de dos mil siete"</dd>
 *
 * <dt>Licencia:</dt>
 * <dd><a href="http://creativecommons.org/licenses/GPL/2.0/deed.es">
 * Este software está sujeto a la CC-GNU GPL</a>
 * </dd>
 *
 * </dl>
 *
 * @author Francisco Cascales <fco@proinf.net>
 * @version 0.01,  8-dic-2007 - Inicio del proyecto
 * @version 0.02, 12-dic-2007 - Cifras en femenino
 * @version 0.03, 17-dic-2007 - Formatear cifras separándolas en grupos
 * @version 0.04, 22-dic-2007 - Múltiplos de millón con preposición "de" antes del concepto:
 *                                "un millón de euros", "dos millones de euros", "un millón mil un euros"
 *                              Las cifras superiores al millón siempre en masculino.
 *                                "doscientos millones doscientas mil personas"
 * @version 0.05,  7-ene-2008 - Mejoras estructurales
 */
 
public class CifrasEnLetras {
 
    /////////////////////////////////////////////
    // CONSTANTES
 
    private final static String PREFIJO_ERROR = "Error: ";
    private final static String COMA = ",";
    private final static String MENOS = "-";
 
    /////////////////////////////////////////////
    // ENUMERACIONES
 
    public enum Genero {
        neutro, masculino, femenino;
        public boolean esMasculino() { return this == masculino; }
        public boolean esFemenino() { return this == femenino; }
        public static Genero desdeNumero(int numero) { if (numero==0) return neutro; else if (numero==1) return masculino; return femenino; }
    };
 
    /////////////////////////////////////////////
    // LISTAS
 
    /** Letras de los números entre el 0 y el 29 */
    public final static String[] listaUnidades = {
        "cero", "un", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve",
        "diez", "once", "doce", "trece", "catorce", "quince", "dieciséis", "diecisiete", "dieciocho", "diecinueve",
        "veinte", "veintiún", "veintidós", "veintitrés", "veinticuatro", "veinticinco", "veintiséis", "veintisiete", "veintiocho", "veintinueve"
    };
    /** Letras de las decenas  */
    public final static String[] listaDecenas = {
        "", "diez", "veinte", "treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa"
    };
    /** Letras de las centenas */
    public final static String[] listaCentenas = {
        "", "cien", "doscientos", "trescientos", "cuatrocientos", "quinientos", "seiscientos", "setecientos", "ochocientos", "novecientos"
    };
    /** Letras en singular de los órdenes de millón */
    public final static String[] listaOrdenesMillonSingular = {
        "", "millón", "billón", "trillón", "cuatrillón", "quintillón",
        "sextillón", "septillón", "octillón", "nonillón", "decillón",
        "undecillón", "duodecillón", "tridecillón", "cuatridecillón", "quidecillón",
        "sexdecillón", "septidecillón", "octodecillón", "nonidecillón", "vigillón"
    };
    /** Letras en plural de los órdenes de millón */
    public final static String[] listaOrdenesMillonPlural = {
        "", "millones", "billones", "trillones", "cuatrillones", "quintillones",
        "sextillones", "septillones", "octillones", "nonillones", "decillones",
        "undecillones", "duodecillones", "tridecillones", "cuatridecillones", "quidecillones",
        "sexdecillones", "septidecillones", "octodecillones", "nonidecillones", "vigillones"
    };
 
    // ORDINALES (Wikipedia: "Nombres de los números en español", "Número ordinal")
 
    /** Letras de los ordinales entre 0º y 19º */
    public final static String[] listaUnidadesOrdinales = {
        "ningún", "primer", "segundo", "tercer", "cuarto", "quinto", "sexto", "séptimo", "octavo", "noveno",
        "décimo", "undécimo", "duodécimo", "decimotercer", "decimocuarto", "decimoquinto", "decimosexto", "decimoséptimo", "decimoctavo", "decimonoveno"
    };
    /** Letras de las decenas ordinales */
    public final static String[] listaDecenasOrdinales = {
        "", "décimo", "vigésimo", "trigésimo", "cuadragésimo", "quincuagésimo", "sexagésimo", "septuagésimo", "octogésimo", "nonagésimo"
    };
    /** Letras de las centenas ordinales */
    public final static String[] listaCentenasOrdinales = {
        "", "centésimo", "ducentésimo", "tricentésimo", "cuadringentésimo", "quingentésimo", "sexcentésimo", "septingentésimo", "octingentésimo", "noningentésimo"
    };
    /** Letras de las potencias de diez ordinales */
    public final static String[] listaPotenciasDiezOrdinales = {
        "", "décimo", "centésimo", "milésimo", "diezmilésimo", "cienmilésimo", "millonésimo"
    };
 
    /////////////////////////////////////////////
    // MÉTODOS PRINCIPALES
 
    /** Convierte a letras los números entre 0 y 29.
     *  Ejemplo: <code>convertirUnidades(21,Genero.femenino)</code> &rarr; <samp>"veintiuna"</samp>
     */
    protected static String convertirUnidades (int unidades, Genero genero) {
        if (unidades == 1) {
            if (genero.esMasculino()) return "uno";
            else if (genero.esFemenino()) return "una";
        }
        else if (unidades == 21) {
            if (genero.esMasculino()) return "veintiuno";
            else if (genero.esFemenino()) return "veintiuna";
        }
        return listaUnidades[unidades];
    }
    /** Convierte a letras las centenas
     * Ejemplo: <code>convertirCentenas(2,Genero.femenino)</code> &rarr; <samp>"doscientas"</samp>
     */
    protected static String convertirCentenas (int centenas, Genero genero) {
        String resultado = listaCentenas[centenas];
        if (genero.esFemenino()) resultado = resultado.replaceAll("iento","ienta");
        return resultado;
    }
 
    /**
     * Primer centenar: del cero al noventa y nueve.
     * <dl>
     * <dt>Ejemplos:</dt>
     * <dd><code>convertirDosCifras(22,Genero.neutro)</code> &rarr; <samp>"veintidós"</samp></dd>
     * </dl>
     * @see #convertirUnidades(int,Genero)
     */
    public static String convertirDosCifras(int cifras, Genero genero) {
        int unidad = cifras % 10;
        int decena = cifras / 10;
        if (cifras < 30) return convertirUnidades(cifras, genero);
        else if (unidad == 0) return listaDecenas[decena];
        else return listaDecenas[decena] + " y " + convertirUnidades(unidad, genero);
    }
    /**
     * Primer millar: del cero al novecientos noventa y nueve.
     * <dl>
     * <dt>Ejemplos:</dt>
     * <dd><code>convertirTresCifras(222,Genero.neutro)</code> &rarr; <samp>"doscientos veintidós"</samp></dd>
     * </dl>
     * @see #convertirDosCifras(int,Genero)
     * @see #convertirCentenas(int,Genero)
     */
    public static String convertirTresCifras(int cifras, Genero genero) {
        int decenas_y_unidades = cifras % 100;
        int centenas = cifras / 100;
        if  (cifras < 100) return convertirDosCifras(cifras, genero);
        else if (decenas_y_unidades == 0) return convertirCentenas(centenas, genero);
        else if (centenas == 1) return "ciento " + convertirDosCifras(decenas_y_unidades, genero);
        else return convertirCentenas(centenas, genero) + " " + convertirDosCifras(decenas_y_unidades, genero);
    }
 
    /**
     * Primer millón: del cero al novecientos noventa y nueve mil novecientos noventa y nueve.
     * <dl>
     * <dt>Ejemplos:</dt>
     * <dd><code>convertirSeisCifras(222222)</code> &rarr; <samp>"doscientos veintidós mil doscientos veintidós"</samp></dd>
     * </dl>
     * @see #convertirTresCifras(int,Genero)
     */
    public static String convertirSeisCifras(int cifras, Genero genero) {
        int primerMillar = cifras % 1000;
        int grupoMiles = cifras / 1000;
        Genero generoMiles = genero.esMasculino() ? Genero.neutro : genero;
        if (grupoMiles == 0) return convertirTresCifras(primerMillar, genero);
        else if (grupoMiles == 1) {
            if (primerMillar == 0) return "mil";
            else return "mil " + convertirTresCifras(primerMillar, genero);
        }
        else if (primerMillar == 0) return convertirTresCifras(grupoMiles, generoMiles) + " mil";
        else return convertirTresCifras(grupoMiles, generoMiles) + " mil " + convertirTresCifras(primerMillar, genero);
    }
 
    /**
     * Números enteros entre el cero y el novecientos noventa y nueve mil novecientos noventa y nueve vigillones... etc, etc.<br />
     * Es decir entre el 0 y el (10<sup>126</sup>)-1 o bien números entre 1 y 126 cifras.<br />
     * Las cifras por debajo del millón pueden ir en masculino o en femenino.
     * <dl>
     * <dt>Ejemplos:</dt>
     * <dd><code>convertirCifrasEnLetras("22222222")</code> &rarr; <samp>"veintidós millones doscientos veintidós mil doscientos veintidós"</samp></dd>
     * <dd><code>convertirCifrasEnLetras("")</code> &rarr; <samp>"No hay ningún número"</samp></dd>
     * <dd><code>convertirCifrasEnLetras(repetirCaracter('9',127))</code> &rarr; <samp>"El número es demasiado grande ya que tiene más de 126 cifras"</samp></dd>
     * <dd><code>convertirCifrasEnLetras("0x")</code> &rarr; <samp>"Uno de los caracteres no es una cifra decimal"</samp></dd>
     * <dd><code>convertirCifrasEnLetras(repetirCaracter('9',126))</code> &rarr; <samp>"novecientos noventa y nueve mil novecientos noventa y nueve vigillones..."</samp></dd>
     * <dd><code>convertirCifrasEnLetras(10^6)</code> &rarr; <samp>"un millón"</samp></dd>
     * <dd><code>convertirCifrasEnLetras(10^12)</code> &rarr; <samp>"un billón"</samp></dd>
     * <dd><code>convertirCifrasEnLetras(10200050)</code> &rarr; <samp>"diez millones doscientos mil cincuenta"</samp></dd>
     * <dd><code>convertirCifrasEnLetras(10001000)</code> &rarr; <samp>"diez millones mil"</samp></dd>
     * <dd><code>convertirCifrasEnLetras("1" + repetirCaracter('0',120))</code> &rarr; <samp>"un vigillón"</samp></dd>
     * <dd><code>convertirCifrasEnLetras("2" + repetirCaracter('0',18))</code> &rarr; <samp>"dos trillones"</samp></dd>
     * <dd><code>convertirCifrasEnLetras("4792347927489", "\n")</code> &rarr; <samp>"..."</samp></dd>
     * <dd><code>convertirCifrasEnLetrasFemeninas("501")</code> &rarr; <samp>"quinientas una"</samp></dd>
     * <dd><code>convertirCifrasEnLetrasFemeninas("240021")</code> &rarr; <samp>"doscientas cuarenta mil veintiuna"</samp></dd>
     * </dl>
     * @see #convertirSeisCifras(int,Genero)
     */
    public static String convertirCifrasEnLetras(String cifras, Genero genero,
        String separadorGruposSeisCifras)
    {
        // Inicialización
        cifras = cifras.trim();
        int numeroCifras = cifras.length();
 
        // Comprobación
        if (numeroCifras == 0) return PREFIJO_ERROR + "No hay ningún número";
        for (int indiceCifra=0; indiceCifra<numeroCifras; ++indiceCifra) {
            char cifra = cifras.charAt(indiceCifra);
            boolean esDecimal = "0123456789".indexOf(cifra) >= 0;
            if (!esDecimal) return PREFIJO_ERROR + "Uno de los caracteres no es una cifra decimal";
        }
        if (numeroCifras > 126) return PREFIJO_ERROR + "El número es demasiado grande ya que tiene más de 126 cifras";
 
        // Preparación
        int numeroGruposSeisCifras = numeroCifras / 6 + Integer.signum(numeroCifras);
        String cerosIzquierda = repetirCaracter('0', numeroGruposSeisCifras * 6 - numeroCifras);
        cifras = cerosIzquierda + cifras;
        int ordenMillon = numeroGruposSeisCifras - 1;
 
        // Procesamiento
        StringBuffer resultado = new StringBuffer();
        for (int indiceGrupo=0; indiceGrupo<numeroGruposSeisCifras*6; indiceGrupo+=6) {
            int seisCifras = Integer.parseInt( cifras.substring(indiceGrupo, indiceGrupo+6) );
            if (seisCifras != 0) {
                if (resultado.length() > 0) resultado.append(separadorGruposSeisCifras);
 
                if (ordenMillon == 0) {
                    resultado.append(convertirSeisCifras(seisCifras, genero));
                }
                else if (seisCifras == 1) {
                    resultado.append("un " + listaOrdenesMillonSingular[ordenMillon]);
                }
                else {
                    resultado.append(convertirSeisCifras(seisCifras, Genero.neutro) +
                        " " + listaOrdenesMillonPlural[ordenMillon]);
                }
            }
            ordenMillon--;
        }
 
        // Finalización
        if (resultado.length() == 0) resultado.append(listaUnidades[0]);
        return resultado.toString();
    }
    /** @see #convertirCifrasEnLetras(String, Genero, String) */
    public static String convertirCifrasEnLetras(String cifras) {
      return convertirCifrasEnLetras(cifras, Genero.neutro, " ");
    }
    public static String convertirCifrasEnLetras(Double cifras) {
    	return convertirCifrasEnLetras(cifras.toString(), Genero.neutro, " ");
      }
    /** @see #convertirCifrasEnLetras(String, Genero, String) */
    public static String convertirCifrasEnLetrasMasculinas(String cifras) {
      return convertirCifrasEnLetras(cifras, Genero.masculino, " ");
    }
    /** @see #convertirCifrasEnLetras(String, Genero, String) */
    public static String convertirCifrasEnLetrasFemeninas(String cifras) {
      return convertirCifrasEnLetras(cifras, Genero.femenino, " ");
    }
 
    /**
     * Expresa un número con decimales y signo en letras
     * acompañado del tipo de medida para la parte entera y la parte decimal.
     * <ul>
     * <li>Los caracteres no numéricos son ignorados.</li>
     * <li>Los múltiplos de millón tienen la preposición <q>de</q> antes de la palabra.</li>
     * <li>El género masculino o femenino sólo puede influir en las cifras inferiores al millón</li>
     * </ul>
     * <dl><dt>Ejemplos:</dt>
     * <dd><code>convertirNumeroEnLetras("-123,45",2)</code> &rarr; <samp>"menos ciento veintitrés con cuarenta y cinco"</samp></dd>
     * <dd><code>convertirNumeroEnLetras("2.000,25", 3, "kilo","gramo")</code> &rarr; <samp>"dos mil kilos con doscientos cincuenta gramos"</samp></dd>
     * <dd><code>convertirNumeroEnLetras("43,005", 3, "kilómetro","metro")</code> &rarr; <samp>"cuarenta y tres kilómetros con cinco metros"</samp></dd>
     * <dd><code>convertirNumeroEnLetras("1.270,23", 2, "euro","céntimo")</code> &rarr; <samp>"mil doscientos setenta euros con veintitrés céntimos"</samp></dd>
     * <dd><code>convertirNumeroEnLetras("1", 2, "euro","céntimo")</code> &rarr; <samp>"un euro con cero céntimos"</samp></dd>
     * <dd><code>convertirNumeroEnLetras("0,678", 2, "euro","céntimo")</code> &rarr; <samp>"cero euros con sesenta y siete céntimos"</samp></dd>
     * <dd><code>convertirNumeroEnLetras("22.000,55", 0, "euro","")</code> &rarr; <samp>"veintidós mil euros"</samp></dd>
     * <dd><code>convertirNumeroEnLetras("-,889")</code> &rarr; <samp>"menos cero con ochocientos ochenta y nueve"</samp></dd>
     * <dd><code>convertirNumeroEnLetras("200",0,"manzana",true)</code> &rarr; <samp>"doscientas manzanas"</samp></dd>
     * <dd><code>convertirNumeroEnLetras("1,5",2,"peseta","céntimo",true,false)</code> &rarr; <samp>"una peseta con cincuenta céntimos"</samp></dd>
     * <dd><code>convertirNumeroEnLetras("300,56",3,"segundo","milésima",false,true)</code> &rarr; <samp>"trescientos segundos con quinientas sesenta milésimas"</samp></dd>
     * <dd><code>convertirNumeroEnLetras("21,21",2,"niño","niña",false,true)</code> &rarr; <samp>"veintiún niños con veintiuna niñas"</samp></dd>
     * <dd><code>convertirNumeroEnLetras("1000000",,"euro")</code> &rarr; <samp>"un millón de euros"</samp></dd>
     * <dd><code>convertirNumeroEnLetras("200.200.200","persona",true)</code> &rarr; <samp>"doscientos millones doscientas mil doscientas personas"</samp></dd>
     * <dd><code>convertirNumeroEnLetras("221.221.221")</code> &rarr; <samp>"doscientos veintiún millones doscientos veintiún mil doscientos veintiuno"</samp></dd>
     * </dl>
     * @param numeroDecimales Si es -1 el número de decimales es automático
     * @see #convertirCifrasEnLetras(String, Genero, String)
     * @see #procesarEnLetras(String, String, String, boolean)
     */
    public static String convertirNumeroEnLetras(String cifras, int numeroDecimales,
       String palabraEnteraSingular, String palabraEnteraPlural, boolean esFemeninaPalabraEntera,
       String palabraDecimalSingular, String palabraDecimalPlural, boolean esFemeninaPalabraDecimal)
    {
        // Limpieza
        cifras = dejarSoloCaracteresDeseados(cifras, "0123456789" + COMA + MENOS);
 
        // Comprobaciones
        int repeticionesMenos = numeroRepeticiones(cifras, MENOS);
        int repeticionesComa = numeroRepeticiones(cifras, COMA);
        if (repeticionesMenos > 1 || (repeticionesMenos == 1 && !cifras.startsWith(MENOS)) )
            return PREFIJO_ERROR + "Símbolo negativo incorrecto o demasiados símbolos negativos";
        else if (repeticionesComa > 1) return PREFIJO_ERROR + "Demasiadas comas decimales";
 
        // Negatividad
        boolean esNegativo = cifras.startsWith(MENOS);
        if (esNegativo) cifras = cifras.substring(1);
 
        // Preparación
        int posicionComa = cifras.indexOf(COMA);
        if (posicionComa == -1) posicionComa = cifras.length();
 
        String cifrasEntera = cifras.substring(0, posicionComa);
        if (cifrasEntera.equals("") || cifrasEntera.equals(MENOS)) cifrasEntera = "0";
        String cifrasDecimal = cifras.substring(Math.min(posicionComa + 1, cifras.length()));
 
        boolean esAutomaticoNumeroDecimales = numeroDecimales < 0;
        if (esAutomaticoNumeroDecimales) {
            numeroDecimales = cifrasDecimal.length();
        }
        else {
            cifrasDecimal = cifrasDecimal.substring(0, Math.min(numeroDecimales, cifrasDecimal.length()));
            String cerosDerecha = repetirCaracter('0', numeroDecimales - cifrasDecimal.length());
            cifrasDecimal = cifrasDecimal + cerosDerecha;
        }
 
        // Cero
        boolean esCero = dejarSoloCaracteresDeseados(cifrasEntera,"123456789").equals("") &&
                         dejarSoloCaracteresDeseados(cifrasDecimal,"123456789").equals("");
 
        // Procesar
        StringBuffer resultado = new StringBuffer();
 
        if (esNegativo && !esCero) resultado.append("menos ");
 
        String parteEntera = procesarEnLetras(cifrasEntera, palabraEnteraSingular, palabraEnteraPlural, esFemeninaPalabraEntera);
        if (parteEntera.startsWith(PREFIJO_ERROR)) return parteEntera;
        resultado.append(parteEntera);
 
        if (!cifrasDecimal.equals("")) {
            String parteDecimal = procesarEnLetras(cifrasDecimal, palabraDecimalSingular, palabraDecimalPlural, esFemeninaPalabraDecimal);
            if (parteDecimal.startsWith(PREFIJO_ERROR)) return parteDecimal;
            resultado.append(" con ");
            resultado.append(parteDecimal);
        }
 
        return resultado.toString();
    }
    /** @see #convertirNumeroEnLetras(String, int, String, String, boolean, String, String, boolean) */
    public static String convertirNumeroEnLetras(String cifras) {
      return convertirNumeroEnLetras(cifras, -1, "","",false,"","",false);
    }
    /** @see #convertirNumeroEnLetras(String, int, String, String, boolean, String, String, boolean) */
    public static String convertirNumeroEnLetras(String cifras, int numeroDecimales) {
        return convertirNumeroEnLetras(cifras, numeroDecimales, "","",false,"","",false);
    }
    /** @see #convertirNumeroEnLetras(String, int, String, String, boolean, String, String, boolean) */
    public static String convertirNumeroEnLetras(String cifras, String palabraEntera) {
      return convertirNumeroEnLetras(cifras, 0, palabraEntera, palabraEntera+"s", false,"","",false);
    }
    /** @see #convertirNumeroEnLetras(String, int, String, String, boolean, String, String, boolean) */
    public static String convertirNumeroEnLetras(String cifras, String palabraEntera, boolean esFemeninaPalabraEntera) {
      return convertirNumeroEnLetras(cifras, 0, palabraEntera, palabraEntera+"s", esFemeninaPalabraEntera,"","",false);
    }
    /** @see #convertirNumeroEnLetras(String, int, String, String, boolean, String, String, boolean) */
    public static String convertirNumeroEnLetras(String cifras, int numeroDecimales, String palabraEntera, String palabraDecimal) {
      return convertirNumeroEnLetras(cifras, numeroDecimales,
          palabraEntera, palabraEntera+"s", false,
          palabraDecimal, palabraDecimal+"s",false);
    }
    /** @see #convertirNumeroEnLetras(String, int, String, String, boolean, String, String, boolean) */
    public static String convertirNumeroEnLetras(String cifras, int numeroDecimales,
        String palabraEntera, String palabraDecimal, boolean esFemeninaPalabraEntera, boolean esFemeninaPalabraDecimal)
    {
      return convertirNumeroEnLetras(cifras, numeroDecimales,
          palabraEntera, palabraEntera+"s", esFemeninaPalabraEntera,
          palabraDecimal, palabraDecimal+"s",esFemeninaPalabraDecimal);
    }
 
    /**
     * Función auxiliar de <code>convertirNumeroEnLetras</code>
     * para procesar por separado la parte entera y la parte decimal
     * @see #convertirCifrasEnLetras(String, boolean, String)
     */
    protected static String procesarEnLetras(String cifras,
        String palabraSingular, String palabraPlural, boolean esFemenina)
    {
        // Género
        Genero genero = Genero.neutro;
        if (esFemenina) genero = Genero.femenino;
        else if (palabraSingular.equals("")) genero = Genero.masculino;
 
        // Letras
        String letras = convertirCifrasEnLetras(cifras, genero, " ");
        if (letras.startsWith(PREFIJO_ERROR)) return letras;
 
        // Propiedades // 7-ene-2008
        boolean esCero = letras.equals(convertirUnidades(0, genero)) || letras.equals(""); //letras.equals("cero") || letras.equals("");
        boolean esUno = letras.equals(convertirUnidades(1, genero)); // letras.equals("un") || letras.equals("una") || letras.equals("uno");
        boolean esMultiploMillon = !esCero && cifras.endsWith("000000");
 
        // Palabra
        String palabra = "";
        if (!palabraSingular.equals("")) {
            if (esUno || palabraPlural.equals(""))
                palabra = palabraSingular;
            else
                palabra = palabraPlural;
        }
 
        // Resultado
        StringBuffer resultado = new StringBuffer();
        resultado.append(letras);
        if (!palabra.equals("")) {
            if (esMultiploMillon) resultado.append (" de ");
            else resultado.append(" ");
            resultado.append(palabra);
        }
        return resultado.toString();
    }
 
    /**
     * Convertir euros en letras
     * <dl>
     * <dt>Ejemplos:</dt>
     * <dd><code>convertirEurosEnLetras("44276598801,2",2)</code> &rarr; <samp>"cuatrocientos noventa y ocho mil un euros con veinte céntimos"</samp></dd>
     * <dd><code>convertirEurosEnLetras(85009)</code> &rarr; <samp>"ochenta y cinco mil nueve euros"</samp></dd>
     * <dd><code>convertirEurosEnLetras(10200.35)</code> &rarr; <samp>"diez mil doscientos euros con treinta y cinco céntimos"</samp></dd>
     * </dl>
     * @see #convertirNumeroEnLetras(String, int, String, String)
     */
    public static String convertirEurosEnLetras(String cifras, int numeroDecimales) {
        return convertirNumeroEnLetras(cifras, numeroDecimales, "euro", "céntimo");
    }
    /** @see #convertirEurosEnLetras(String, int) */
    public static String convertirEurosEnLetras(long euros) {
        String cifras = String.valueOf(euros);
        return convertirEurosEnLetras(cifras, 0);
    }
    /** @see #convertirEurosEnLetras(String, int) */
    public static String convertirEurosEnLetras(double euros) {
        String cifras = String.valueOf(euros).replace('.', ',');
        return convertirEurosEnLetras(cifras, 2);
    }
 
 
 
    /**
     * <dl>
     * <dt>Ejemplos:</dt>
     * <dd><code>11</code> &rarr; <samp>"undécimo"</samp></dd>
     * <dd><code>2222</code> &rarr; <samp>"dosmilésimo ducentésimo vigésimo segundo"</samp></dd>
     * </dl>
     */
    public static String convertirOrdinalesEnLetras(String cifras, Genero genero) {
        return "";
    }
 
    /**
     * Convertir años en letras.
     * Ejemplos: <samp>"cuatro años", "un lustro", "una década", "dos décadas", "un siglo", "un milenio"</samp>
     */
    public static String convertirAñosEnLetras(String cifras) {
        return "";
    }
 
    /**
     * Convertir fechas en letras.
     * <dl>
     * <dt>Ejemplos:</dt>
     * <dd>"21-XI-2007" &rarr; "veintiuno de noviembre de dos mil siete"</dd>
     * </dl>
     */
    public static String convertirFechasEnLetras(String fecha) {
        return "";
    }
 
    /////////////////////////////////////////////
    // FUNCIONES AUXILIARES
 
    /**
     * Crea un texto repitiendo el caracter el número de veces indicado
     * <dl>
     * <dt>Ejemplos:</dt>
     * <dd><code>repetirCaracter('0',4)</code> &rarr; <samp>"0000"</dd>
     * </dl>
     */
    public static String repetirCaracter(char caracter, int veces) {
        char[] arreglo = new char [veces];
        java.util.Arrays.fill(arreglo, caracter);
        return new String(arreglo);
    }
 
    /**
     * Borra todos los caracteres del texto que no sea alguno de los caracteres deseados.
     * <dl>
     * <dt>Ejemplos:</dt>
     * <dd><code>dejarSoloCaracteresDeseados("89.500.400","0123456789")</code> &rarr; <samp>"89500400"</samp></dd>
     * <dd><code>dejarSoloCaracteresDeseados("ABC-000-123-X-456","0123456789")</code> &rarr; <samp>"000123456"</samp></dd>
     * </dl>
     */
    public static String dejarSoloCaracteresDeseados(String texto, String caracteresDeseados) {
        int indice = 0;
        StringBuffer resultado = new StringBuffer(texto);
        while (indice < resultado.length()) {
            char caracter = resultado.charAt(indice);
            if (caracteresDeseados.indexOf(caracter) < 0) resultado.deleteCharAt(indice);
            else indice++;
        }
        return resultado.toString();
    }
 
    /**
     * Cuenta el número de repeticiones en el texto de los caracteres indicados
     * <dl>
     * <dt>Ejemplos:</dt>
     * <dd><code>numeroRepeticiones("89.500.400","0")</code> &rarr; <samp>4</samp></dd>
     * </dl>
     */
    public static int numeroRepeticiones(String texto, String caracteres) {
        int resultado = 0;
        for (int indice=0; indice<texto.length(); ++indice) {
            char caracter = texto.charAt(indice);
            if (caracteres.indexOf(caracter) >= 0) resultado++;
        }
        return resultado;
    }
 
    /**
     * Separa las cifras en grupos de 6 con subrayados y los grupos de 6 en grupos de 2 con punto
     * <dl>
     * <dt>Ejemplos:</dt>
     * <dd><code>formatearCifras("-4739249,2")</code> &rarr; <samp>"-4_739.249,2"</samp></dd>
     * </dl>
     */
    public static String formatearCifras(String cifras) {
        cifras = dejarSoloCaracteresDeseados(cifras, "0123456789" + COMA + MENOS);
        if (cifras.length() == 0) return cifras;
 
        boolean esNegativo = cifras.startsWith(MENOS);
        if (esNegativo) cifras = cifras.substring(1);
 
        int posicionComa = cifras.indexOf(COMA);
        boolean esDecimal = posicionComa >= 0;
 
        if (!esDecimal) posicionComa = cifras.length();
        String cifrasEntera = cifras.substring(0, posicionComa);
        String cifrasDecimal = "";
 
        if (esDecimal) cifrasDecimal = cifras.substring(Math.min(posicionComa + 1, cifras.length()));
        if (cifrasEntera.equals("")) cifrasEntera = "0";
 
        StringBuffer resultado = new StringBuffer();
        int numeroCifras = cifrasEntera.length();
        //int numeroGruposTresCifras = numeroCifras / 3 + Integer.signum(numeroCifras);
        boolean par = true;
 
        for (int indice=0; indice<numeroCifras; indice+=3) {
            int indiceGrupo = numeroCifras - indice;
            String tresCifras = cifras.substring(Math.max(indiceGrupo-3,0), indiceGrupo);
            if (indice > 0) {
                resultado.insert(0, par==true?'.':'_');
                par = !par;
            }
            resultado.insert(0, tresCifras);
        }
        if (esNegativo) resultado.insert(0, MENOS);
        if (esDecimal) resultado.append(COMA + cifrasDecimal);
 
        return resultado.toString();
    }
 
}
 
 