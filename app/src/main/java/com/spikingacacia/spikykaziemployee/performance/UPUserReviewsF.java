package com.spikingacacia.spikykaziemployee.performance;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spikingacacia.spikykaziemployee.R;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UPUserReviewsF extends Fragment
{

    //private static final String ARG_USER = "user";
    //private int mUser;
    private OnListFragmentInteractionListener mListener;
    private  RecyclerView recyclerView;
    private UPUserReviewsRecyclerViewAdapter uPUserReviewsRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UPUserReviewsF() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UPUserReviewsF newInstance() {
        UPUserReviewsF fragment = new UPUserReviewsF();
        //Bundle args = new Bundle();
        //args.putInt(ARG_USER, user);
       // fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            //mUser = getArguments().getInt(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_upuser_review_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            UPUserReviewsContent content=new UPUserReviewsContent();
            uPUserReviewsRecyclerViewAdapter =new UPUserReviewsRecyclerViewAdapter(content.ITEMS, mListener,getContext());
            recyclerView.setAdapter(uPUserReviewsRecyclerViewAdapter);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.upuser_reviews_menu, menu);
        final MenuItem searchItem=menu.findItem(R.id.action_search);
        final SearchView searchView=(SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                UPUserReviewsRecyclerViewAdapter adapter=(UPUserReviewsRecyclerViewAdapter) recyclerView.getAdapter();
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                UPUserReviewsRecyclerViewAdapter adapter=(UPUserReviewsRecyclerViewAdapter) recyclerView.getAdapter();
                adapter.filter(newText);
                return true;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onUserReviewClicked(UPUserReviewsContent.ReviewItem item);
    }
}
