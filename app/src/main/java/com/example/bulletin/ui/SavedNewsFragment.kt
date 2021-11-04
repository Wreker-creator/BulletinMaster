package com.example.bulletin.ui

import android.app.AlertDialog
import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bulletin.MainActivity
import com.example.bulletin.R
import com.example.bulletin.adapters.SavedAdapter
import com.example.bulletin.viewModel.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class SavedNewsFragment : Fragment() {

    private lateinit var savedViewModel : NewsViewModel

    //recycler
    private lateinit var savedRecycler : RecyclerView
    private lateinit var savedAdapter : SavedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_saved_news, container, false)

        savedViewModel = (activity as MainActivity).viewModel1

        //recycler
        savedRecycler = view.findViewById(R.id.SavedFragmentRecyclerView)
        savedRecycler.layoutManager = LinearLayoutManager(activity)
        savedAdapter = SavedAdapter()
        savedRecycler.adapter = savedAdapter

        //we are calling our own custom function setOnItemClickListener here.
        //which takes an article and returns unit. So what we are doing here
        // is we take the article attach it to a bundle and then pass it along
        //with the navigation components which will then handle the transition for
        //us and carry the article with it to the article fragment
        savedAdapter.setOnSavedItemClickListener {
            val bundle = Bundle().apply{
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_savedNewsFragment_to_articleWebViewFragment, bundle)
        }

        //It is a cool feature to be able to delete something from a list in an app
        //by simply swiping it left or right which is what we are trying to implement here
        //Also,     This is just a reference. The real call happens down below
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.LEFT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            // here we are creating a snackbar which is a brother of toast message
            //but there is a difference. here i can add any action such as undo
            //which when pressed will act as button and will do what its functionality
            //says in the code
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = savedAdapter.savedDiffer.currentList[position]
                savedViewModel.deleteArticle(article)
                Snackbar.make(view, "Successfully deleted.", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        savedViewModel.saveArticle(article)
                    }.show()
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float,
                dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {

                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(activity as MainActivity, R.color.red))
                    .addActionIcon(R.drawable.delete_white)
                    .create()
                    .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

        }

        //ItemTouchHelper Call
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(savedRecycler)
        }

        //when we save a news we call an observer on it to submit the article we want
        //to save which is passed as a list of articles. We submit this to the
        //async list differ which then checks whether the article we are adding is different
        //or not and then decides whether it is added to the database or not
        savedViewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
            savedAdapter.savedDiffer.submitList(articles)
        })

        //Delete All Saved Articles
        view.findViewById<ImageButton>(R.id.DeleteAllSavedArticles).setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setPositiveButton("Yes"){_, _ ->
                savedViewModel.deleteAllArticles()
                Snackbar.make(view, "Successfully Deleted.", Snackbar.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("NO"){_, _->}
            builder.setTitle("Delete All Articles?")
            builder.setIcon(R.drawable.ic_baseline_delete_24)
            builder.setMessage("Are you sure you want to Delete all of the saved Articles?")
            builder.create().show()
        }

        //Go back to main fragment
        view.findViewById<ImageButton>(R.id.BackToMain).setOnClickListener {
            activity?.onBackPressed()
        }

        return view
    }

}