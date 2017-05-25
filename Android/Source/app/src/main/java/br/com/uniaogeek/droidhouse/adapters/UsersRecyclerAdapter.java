package br.com.uniaogeek.droidhouse.adapters;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.uniaogeek.droidhouse.R;
import br.com.uniaogeek.droidhouse.model.Usuario;

import java.util.List;

/**
 * Created by lalit on 10/10/2016.
 */

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.UserViewHolder> {

    private List<Usuario> listUsuarios;

    public UsersRecyclerAdapter(List<Usuario> listUsuarios) {
        this.listUsuarios = listUsuarios;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflating recycler item view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_recycler, parent, false);

        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.textViewName.setText(listUsuarios.get(position).getNome());
        holder.textViewEmail.setText(listUsuarios.get(position).getEmail());
        holder.textViewPassword.setText(listUsuarios.get(position).getSenha());
        holder.textViewPerfil.setText(listUsuarios.get(position).getPerfil());
    }

    @Override
    public int getItemCount() {
        Log.v(UsersRecyclerAdapter.class.getSimpleName(),""+ listUsuarios.size());
        return listUsuarios.size();
    }


    /**
     * ViewHolder class
     */
    public class UserViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView textViewName;
        public AppCompatTextView textViewEmail;
        public AppCompatTextView textViewPassword;
        public AppCompatTextView textViewPerfil;

        public UserViewHolder(View view) {
            super(view);
            textViewName = (AppCompatTextView) view.findViewById(R.id.textViewName);
            textViewEmail = (AppCompatTextView) view.findViewById(R.id.textViewEmail);
            textViewPassword = (AppCompatTextView) view.findViewById(R.id.textViewPassword);
            textViewPerfil = (AppCompatTextView) view.findViewById(R.id.textViewPerfil);
        }
    }

}
