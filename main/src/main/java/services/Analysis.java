    package services;

    /**
     * Reprezintă o analiză medicală individuală, cu toate informațiile relevante:
     * denumire, rezultat, interval de referință și nivelul de severitate.
     * <p>
     * Această clasă este utilizată pentru a modela rezultatele extrase dintr-un răspuns JSON
     * generat de ChatGPT pe baza unor date medicale.
     * </p>
     *
     * @author [Lucian]
     */
    public class Analysis {
        private final String denumireAnaliza;
        private final double rezultat;
        private final String intervalReferinta;
        private final String severitate;

        /**
         * Creează o instanță a clasei {@code Analysis} cu toate câmpurile necesare.
         *
         * @param denumireAnaliza     Numele analizei medicale (ex: Hemoglobină, Colesterol etc.)
         * @param rezultat            Valoarea numerică a rezultatului obținut în urma analizei
         * @param intervalReferinta   Intervalul de referință acceptat pentru această analiză (ex: "13.5-17.5")
         * @param severitate          Gradul de abatere față de normal (ex: "Normal", "Low", "Mildly Elevated")
         */

        public Analysis(String denumireAnaliza, double rezultat, String intervalReferinta, String severitate) {
            this.denumireAnaliza = denumireAnaliza;
            this.rezultat = rezultat;
            this.intervalReferinta = intervalReferinta;
            this.severitate = severitate;

        }

        /**
         * @return Numele analizei medicale
         */
        public String getDenumireAnaliza() {
            return denumireAnaliza;
        }

        /**
         * @return Valoarea numerică a rezultatului analizei
         */
        public double getRezultat() {
            return rezultat;
        }

        /**
         * @return Intervalul de referință pentru analiza medicală
         */
        public String getIntervalReferinta() {
            return intervalReferinta;
        }

        /**
         * @return Gradul de severitate al rezultatului față de intervalul normal
         */
        public String getSeveritate() {
            return severitate;
        }
    }