package com.example.splashactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SearchAcitivy extends AppCompatActivity {

    private SearchView searchView;
    private TextView textView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_acitivy);
        searchView=findViewById(R.id.searchView);
        textView=findViewById(R.id.t1);
        recyclerView=findViewById(R.id.r1);


        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

       List<WishlistModel>list=new ArrayList<>();
       List<String >ids=new ArrayList<>();

        Adapter adapter=new Adapter(list,false);
        adapter.setFromSearch(true);
        recyclerView.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                list.clear();
                ids.clear();
                String[] tags=s.toLowerCase().split(" ");
                for(String tag:tags){
                    tag.trim();
                    FirebaseFirestore.getInstance().collection("PRODUCTS").whereArrayContains("tags",tag).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        for(DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                                            WishlistModel model=new WishlistModel(documentSnapshot.getId(), (String) documentSnapshot.get("product_image_1").toString()
                                                    , documentSnapshot.get("product_title").toString()
                                                    , (long) documentSnapshot.get("free_coupens")
                                                    , documentSnapshot.get("average_rating").toString()
                                                    , (long) documentSnapshot.get("total_ratings")
                                                    , documentSnapshot.get("product_price").toString()
                                                    , documentSnapshot.get("cutted_price").toString()
                                                    , (boolean) documentSnapshot.get("COD")
                                                    , true);
                                            model.setTags((ArrayList<String>)documentSnapshot.get("tags"));

                                            if(!ids.contains(model.getProductId())){
                                                list.add(model);
                                                ids.add(model.getProductId());
                                            }

                                        }
                                        if(tag.equals(tags[tags.length-1])){
                                            if(list.size()==0) {
                                       textView.setVisibility(View.VISIBLE);
                                       recyclerView.setVisibility(View.GONE);
                                            }
                                            else{
                                                textView.setVisibility(View.GONE);
                                                recyclerView.setVisibility(View.VISIBLE);
                                            }
                                            adapter.getFilter().filter(s);
                                        }

                                    }else{
                                        String error=task.getException().getMessage();
                                        Toast.makeText(SearchAcitivy.this,error,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }
    class Adapter extends WishlistAdapter implements Filterable{

       private List<WishlistModel>originalList;

        public Adapter(List<WishlistModel> wishlistModelList, Boolean wishlist) {
            super(wishlistModelList, wishlist);
            originalList=wishlistModelList;

        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults results=new FilterResults();
                     List<WishlistModel>filteredList=new ArrayList<>();
                    String[] tags=charSequence.toString().toLowerCase().split(" ");

                    for(WishlistModel model:originalList){
                        ArrayList<String>presentTags=new ArrayList<>();
                        for(String tag:tags){
                            if(model.getTags().contains(tags)){
                                presentTags.add(tag);
                            }
                        }
                        model.setTags(presentTags);
                    }

                    for(int i =tags.length-1;i>0;i--){
                        for(WishlistModel model:originalList){
                            if(model.getTags().size()==i){
                                filteredList.add(model);

                            }
                        }
                    }
                    results.values=filteredList;
                    results.count=filteredList.size();
                    return results;

                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                  if(filterResults.count>0){
                      setWishlistModelList((List<WishlistModel>)filterResults.values);

                  }
                 notifyDataSetChanged();
                }
            };
        }
    }
}