package com.kev.mixmaster.mixmaster.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.kev.mixmaster.mixmaster.GridSizer;
import com.kev.mixmaster.mixmaster.R;
import com.kev.mixmaster.mixmaster.RecipeButtonAdapter;
import com.kev.mixmaster.mixmaster.activity.ViewRecipeActivity;
import com.kev.mixmaster.mixmaster.events.EventListener;
import com.kev.mixmaster.mixmaster.events.FavoriteEvent;
import com.kev.mixmaster.mixmaster.model.Recipe;
import com.kev.mixmaster.mixmaster.storage.FavoritesStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A fragment for browsing a collection of recipes. Takes an optional category.
 */
public class BrowseFavoritesFragment extends Fragment implements EventListener<FavoriteEvent> {
    private RecipeButtonAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.browse_recipes_fragment, container, false);

        GridView recipeGrid = (GridView) view.findViewById(R.id.recipes);
        recipeGrid.setNumColumns(GridSizer.getDesiredNumCols(getActivity().getWindowManager()));

        final List<Recipe> recipesToDisplay = new ArrayList<>(FavoritesStorage.getAllFavorites(getActivity()));
        Collections.sort(recipesToDisplay, (a, b) -> a.getName().compareTo(b.getName()));

        recipeGrid.setAdapter(adapter = new RecipeButtonAdapter(getActivity(), recipesToDisplay));
        recipeGrid.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent = new Intent(getContext(), ViewRecipeActivity.class);
            intent.putExtra("recipe", adapter.getItem(position));
            startActivity(intent);
        });

        FavoriteEvent.MANAGER.addListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FavoriteEvent.MANAGER.removeListener(this);
    }

    @Override
    public void consume(FavoriteEvent event) {
        if (event.isFavorite())
            adapter.add(event.getRecipe());
        else
            adapter.remove(event.getRecipe());
    }
}
