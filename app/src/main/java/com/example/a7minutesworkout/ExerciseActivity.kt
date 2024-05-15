package com.example.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.a7minutesworkout.databinding.ActivityExerciseBinding
import java.util.Locale

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var restTimer: CountDownTimer? = null
    private var restProgress = 0

    // TODO(Step 2 - Adding a variables for the 30 seconds Exercise timer.)
    // START
    private var exerciseTimer: CountDownTimer? = null // Variable for Exercise Timer and later on we will initialize it.
    private var exerciseProgress = 0 // Variable for the exercise timer progress. As initial value the exercise progress is set to 0. As we are about to start.
    // END
    private var exerciseTimerDuration:Long = 30
    // TODO(Step 6 - The Variable for the exercise list and current position of exercise here it is -1 as the list starting element is 0.)
    // START
    private var exerciseList: ArrayList<ExerciseModel>? = null // We will initialize the list later.
    private var currentExercisePosition = -1 // Current Position of Exercise.
    private var binding: ActivityExerciseBinding? = null
    private var tts: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarExercise)

        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener {
            onBackPressed()
        }
        // TODO(Step 7 - Initializing and Assigning a default exercise list to our list variable.)
        // START
        exerciseList = Constants.defaultExerciseList()
        // END
        tts = TextToSpeech(this, this)
        setupRestView()

    }
    private fun setupRestView() {

        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.upcomingLabel?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE


        /**
         * Here firstly we will check if the timer is running the and it is not null then cancel the running timer and start the new one.
         * And set the progress to initial which is 0.
         */
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }
        // Here we have set the upcoming exercise name to the text view
        // Here as the current position is -1 by default so to selected from the list it should be 0 so we have increased it by +1.
        binding?.tvUpcomingExerciseName?.text = exerciseList!![currentExercisePosition + 1].getName()

        // This function is used to set the progress details.
        setRestProgressBar()
    }
    // END
    private fun setupExerciseView() {

        // Here according to the view make it visible as this is Exercise View so exercise view is visible and rest view is not.
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.INVISIBLE
        binding?.upcomingLabel?.visibility = View.INVISIBLE

        /**
         * Here firstly we will check if the timer is running and it is not null then cancel the running timer and start the new one.
         * And set the progress to the initial value which is 0.
         */
        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        speakOut(exerciseList!![currentExercisePosition].getName())
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = (exerciseList!![currentExercisePosition].getName())

        setExerciseProgressBar()
    }
    // END

    //TODO(Step 2 - Setting up the 10 seconds timer for rest view and updating it continuously.)-->
    //START
    /**
     * Function is used to set the progress of timer using the progress
     */
    private fun setRestProgressBar() {

        binding?.progressBar?.progress = restProgress // Sets the current progress to the specified value.

        /**
         * @param millisInFuture The number of millis in the future from the call
         *   to {#start()} until the countdown is done and {#onFinish()}
         *   is called.
         * @param countDownInterval The interval along the way to receive
         *   {#onTick(long)} callbacks.
         */
        // Here we have started a timer of 10 seconds so the 10000 is milliseconds is 10 seconds and the countdown interval is 1 second so it 1000.
        restTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++ // It is increased by 1
                binding?.progressBar?.progress = 10 - restProgress // Indicates progress bar progress
                binding?.tvTimer?.text =
                    (10 - restProgress).toString()  // Current progress is set to text view in terms of seconds.
            }

            override fun onFinish() {
                // When the 10 seconds will complete this will be executed.
                currentExercisePosition++
                setupExerciseView()
            }
        }.start()
    }
    //END

    private fun setExerciseProgressBar() {

        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {
                // TODO(Step 10 - Updating the view after completing the 30 seconds exercise.)
                // START
                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    setupRestView()
                } else {

                    Toast.makeText(
                        this@ExerciseActivity,
                        "Congratulations! You have completed the 7 minutes workout.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                // END
            }
        }.start()
    }


    //TODO(Step 5 - Destroying the timer when closing the activity or app.)-->
    //START
    /**
     * Here in the Destroy function we will reset the rest timer if it is running.
     */
    public override fun onDestroy() {
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }

        super.onDestroy()
        binding = null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts?.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            }

        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }
    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}