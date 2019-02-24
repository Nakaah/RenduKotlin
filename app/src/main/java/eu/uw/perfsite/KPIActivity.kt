package eu.uw.perfsite

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.io.BufferedReader
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

private const val EXTRA_ENTRIES = "path"

private const val ENTRY_ANNEE = 0
private const val ENTRY_APRAREIL = 1
private const val ENTRY_COMMANDES = 2
private const val ENTRY_IMPRESSIONS = 3
private const val ENTRY_CLICKS = 4
private const val ENTRY_COUTS = 5
private const val ENTRY_PM = 6
private const val ENTRY_CA = 7
private const val ENTRY_MONTH = 9

class KPIActivity : AppCompatActivity() {

    private var entries = ArrayList<monthPerf>()
    private var results = ArrayList<String>()
    private var path = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupere le chemin de la precedente activité
        path = intent.extras[EXTRA_ENTRIES] as String
        setContentView(R.layout.activity_kpi)

        // Récupere les données du fichier et les mets dans *entries*
        readFile(path)
    }

    private fun readFile(path : String){
        var fileReader: BufferedReader? = null

        try {
            var line: String?
            val fr = FileReader(path)
            fileReader = BufferedReader(fr)

            fileReader.readLine()

            line = fileReader.readLine()
            while (line != null) {
                val tokens = line.split(";")
                if (tokens.isNotEmpty()) {
                    val customer = monthPerf(
                            tokens[ENTRY_ANNEE],
                            tokens[ENTRY_APRAREIL].toLowerCase(),
                            tokens[ENTRY_COMMANDES].replace(',', '.').toFloat(),
                            Integer.parseInt(tokens[ENTRY_IMPRESSIONS]),
                            Integer.parseInt(tokens[ENTRY_CLICKS]),
                            tokens[ENTRY_COUTS].replace(',', '.').toFloat(),
                            tokens[ENTRY_PM].replace(',', '.').replace("--", "0").toFloat(),
                            tokens[ENTRY_CA].replace(',', '.').toFloat(),
                            tokens[ENTRY_MONTH])
                    entries.add(customer)
                }

                line = fileReader.readLine()
            }

            for (entry in entries) {
                Log.w(null, entry.toString())
            }
        } catch (e: Exception) {
            Toast.makeText(baseContext, "Reading CSV Error!", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        } finally {
            try {
                fileReader?.close()
            } catch (e: IOException) {
                Toast.makeText(baseContext, "Close Error!", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun writeFile(){
        var fileWriter: FileWriter? = null

        try {
            fileWriter = FileWriter(path.substring(0, path.lastIndexOf('/')) + "/result.csv")

            for (elem in results) {
                val tmp = elem.split(":")
                fileWriter.append(tmp[0])
                fileWriter.append(';')
                fileWriter.append(tmp[1])
                fileWriter.append('\n')
            }

            Log.w(null, "Write CSV successfully!")
        } catch (e: Exception) {
            Log.w(null,"Writing CSV error!")
            e.printStackTrace()
        } finally {
            try {
                fileWriter!!.flush()
                fileWriter.close()
            } catch (e: IOException) {
                Log.w(null,"Flushing/closing error!")
                e.printStackTrace()
            }
        }
    }

    // Fonctions pour calculs
    private fun ca(annee:String,month:String,appareil:String): Double {
        var ca = 0.0
        var elem = 0
        entries.forEach {
            if (month == "" || it._month.contains(month)) {
                if (annee == "" || it._annee.contains(annee)) {
                    if (appareil == "" || it._appareil.contains(appareil)) {
                        ca += it._ca
                        elem += 1
                    }
                }
            }
        }
        if (elem == 0)
            return 0.0
        else {
            return ca
        }
    }
    private fun roi(annee:String,month:String,appareil:String): Double {
        var ca = 0.0
        var cout = 0.0
        var elem = 0
        entries.forEach {
            if (month == "" || it._month.contains(month)) {
                if (annee == "" || it._annee.contains(annee)) {
                    if (appareil == "" || it._appareil.contains(appareil)) {
                        ca += it._ca
                        cout += it._cout
                        elem += 1
                    }
                }
            }
        }
        if (elem == 0)
            return 0.0
        else {
            val roi = ca / cout
            return roi
        }
    }

    private fun panierMoyen(annee:String,month:String,appareil:String): Double {
        var ca = 0.0
        var commandes = 0.0
        var elem = 0
        entries.forEach {
            if (month == "" || it._month.contains(month)) {
                if (annee == "" || it._annee.contains(annee)) {
                    if (appareil == "" || it._appareil.contains(appareil)) {
                        ca += it._ca
                        commandes += it._commandes
                        elem += 1
                    }
                }
            }
        }
        if (elem == 0)
            return 0.0
        else {
            val pm = ca / commandes
            return commandes
        }
    }

    private fun coutCicks(annee:String,month:String,appareil:String): Double {
        var cout = 0.0
        var clics = 0
        var elem = 0
        entries.forEach {
            if (month == "" || it._month.contains(month)) {
                if (annee == "" || it._annee.contains(annee)) {
                    if (appareil == "" || it._appareil.contains(appareil)) {
                        cout += it._cout
                        clics += it._clics
                        elem += 1
                    }
                }
            }
        }
        if (elem == 0)
            return 0.0
        else {
            val cc = cout / clics
            return cc
        }
    }
    private fun tauxCicks(annee:String,month:String,appareil:String): Double {
        var impression = 0
        var clics = 0
        var elem = 0
        entries.forEach {
            if (month == "" || it._month.contains(month)) {
                if (annee == "" || it._annee.contains(annee)) {
                    if (appareil == "" || it._appareil.contains(appareil)) {
                        impression += it._impressions / 100
                        clics += it._clics / 100
                        elem += 1
                    }
                }
            }
        }
        if (elem == 0)
            return 0.0
        else {
            val tc = (clics / impression).toDouble()
            return tc * 100
        }
    }

    // Click listeners //

    @SuppressLint("SetTextI18n")
    fun caClick(my_view: View) {
        val month = findViewById<EditText>(R.id.month).text.toString().toLowerCase()
        val year = findViewById<EditText>(R.id.year).text.toString().toLowerCase()
        val appareil = findViewById<EditText>(R.id.appareil).text.toString().toLowerCase()
        val result = findViewById<TextView>(R.id.result)

        result.text = "CA : " + ca(year, month, appareil).toString()
    }

    @SuppressLint("SetTextI18n")
    fun roiClick(my_view: View) {
        val month = findViewById<EditText>(R.id.month).text.toString().toLowerCase()
        val year = findViewById<EditText>(R.id.year).text.toString().toLowerCase()
        val appareil = findViewById<EditText>(R.id.appareil).text.toString().toLowerCase()
        val result = findViewById<TextView>(R.id.result)

        result.text = "ROI : " + roi(year, month, appareil).toString()
    }

    @SuppressLint("SetTextI18n")
    fun pmClick(my_view: View) {
        val month = findViewById<EditText>(R.id.month).text.toString().toLowerCase()
        val year = findViewById<EditText>(R.id.year).text.toString().toLowerCase()
        val appareil = findViewById<EditText>(R.id.appareil).text.toString().toLowerCase()
        val result = findViewById<TextView>(R.id.result)

        result.text = "PM : " + panierMoyen(year, month, appareil).toString()
    }

    @SuppressLint("SetTextI18n")
    fun tcClick(my_view: View) {
        val month = findViewById<EditText>(R.id.month).text.toString().toLowerCase()
        val year = findViewById<EditText>(R.id.year).text.toString().toLowerCase()
        val appareil = findViewById<EditText>(R.id.appareil).text.toString().toLowerCase()
        val result = findViewById<TextView>(R.id.result)

        result.text = "TC : " + tauxCicks(year, month, appareil).toString()
    }

    @SuppressLint("SetTextI18n")
    fun ccClick(my_view: View) {
        val month = findViewById<EditText>(R.id.month).text.toString().toLowerCase()
        val year = findViewById<EditText>(R.id.year).text.toString().toLowerCase()
        val appareil = findViewById<EditText>(R.id.appareil).text.toString().toLowerCase()
        val result = findViewById<TextView>(R.id.result)

        result.text = "CC : " + coutCicks(year, month, appareil).toString()
    }

    // Récupèration des listes des années et des appareils dans les entries
    // Et affichage des demandes du sujets

    private var years = ArrayList<String>()
    private var months = ArrayList<String>()
    private var appareils = ArrayList<String>()

    @SuppressLint("SetTextI18n")
    fun runClick(my_view: View) {
        results.clear()
        months.add("janvier")
        months.add("fevrier")
        months.add("mars")
        months.add("avril")
        months.add("mai")
        months.add("juin")
        months.add("juillet")
        months.add("aout")
        months.add("septembre")
        months.add("octobre")
        months.add("novembre")
        months.add("decembre")
        entries.forEach{
            if (it._annee in years){
            }
            else
                years.add(it._annee)
            if (it._appareil in appareils){
            }
            else
                appareils.add(it._appareil)
        }

        // Le chiffre d’affaires par mois par année

        years.forEach{
            val tmp = it
            months.forEach{
                results.add("CA " + it + " " + tmp + ":"+ ca(tmp, it, "").toString())
            }
        }

        // Le chiffre d’affaires par appareil

        appareils.forEach{
            results.add("CA " + it + ":"+ ca("", "", it).toString())
        }

        // Le panier moyen : Chiffre d’affaires / Commandes

        results.add("CA Global:" + panierMoyen("", "", "").toString())

        // Le coût par clic : Coût / Clics

        results.add("CC Global:" + coutCicks("", "", "").toString())

        // Le taux de clic : (Clics /impression) * 100

        results.add("TC Global:" + tauxCicks("", "", "").toString())

        // Le ROI : CA Total / Coût

        results.add("ROI Global:" + roi("", "", "").toString())

        // Le ROI segmenté par appareil et par mois (uniquement sur 2017)

        appareils.forEach{
            val tmp = it
            months.forEach{
               results.add("ROI " + tmp + " " + it + ":"+ ca("", it, tmp).toString())
            }
        }

        writeFile()

        val result = findViewById<TextView>(R.id.result)

        result.text = "Results saved in " + path.substring(0, path.lastIndexOf('/')) + "/result.csv"
    }

}
