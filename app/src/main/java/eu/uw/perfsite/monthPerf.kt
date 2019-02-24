package eu.uw.perfsite

class monthPerf(var _annee: String,
                var _appareil: String,
                var _commandes: Float,
                var _impressions: Int,
                var _clics: Int,
                var _cout: Float,
                var _pm: Float,
                var _ca: Float,
                var _month: String) {

    override fun toString(): String {
        return "Entry [" + _month + " " + _annee + ", ca=>" + _ca + " (=" + _appareil + ")]"
    }
}
