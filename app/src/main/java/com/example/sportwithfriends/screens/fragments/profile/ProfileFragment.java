package com.example.sportwithfriends.screens.fragments.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sportwithfriends.R;
import com.example.sportwithfriends.screens.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private Button buttonSignOut;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonSignOut = requireActivity().findViewById(R.id.buttonSignOut);

        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireActivity(), LogInActivity.class);
                startActivity(intent);
                onDestroy();
            }
        });

//        imageViewAvatar = requireActivity().findViewById(R.id.imageViewAvatarProfile);
//        imageViewFullAvatar = requireActivity().findViewById(R.id.imageViewFullAvatar);
//
//        buttonEditProfile = requireActivity().findViewById(R.id.buttonEditProfile);
//        textViewStatus = requireActivity().findViewById(R.id.textViewStatus);
//
//        dialogEditStatus = new Dialog(requireActivity());
//        dialogEditStatus.setContentView(R.layout.dialog_edit_status);
//
//        recyclerViewFriends = requireActivity().findViewById(R.id.recyclerViewFriends);
//        friendAdapter = new FriendAdapter();
//
//        recyclerViewUsersExercises = requireActivity().findViewById(R.id.recyclerViewUserExercises);
//        exerciseAdapter = new ExerciseAdapter();
//
//        List<Friend> friends = Lists.getFriends();
//        List<Exercise> exercises = Lists.getExercises();
//
//        friendAdapter.setFriends(friends);
//        recyclerViewFriends.setAdapter(friendAdapter);
//        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
//
//        exerciseAdapter.setExercises(exercises);
//        recyclerViewUsersExercises.setAdapter(exerciseAdapter);
//        recyclerViewUsersExercises.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
//
//        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                imageViewFullAvatar.setVisibility(View.VISIBLE);
//            }
//        });
//
//        imageViewFullAvatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                imageViewFullAvatar.setVisibility(View.GONE);
//            }
//        });
//
//        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        textViewStatus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                editStatus();
//            }
//        });
    }

//    private void editStatus() {
//        dialogEditStatus.show();
//        Button buttonSave = dialogEditStatus.findViewById(R.id.buttonSaveStatus);
//        buttonSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                EditText editTextStatus = dialogEditStatus.findViewById(R.id.editTextStatus);
//                String status = editTextStatus.getText().toString();
//                if (!status.isEmpty()) {
//                    textViewStatus.setText(status);
//                }
//                dialogEditStatus.cancel();
//            }
//        });
//
//        Button buttonCancel = dialogEditStatus.findViewById(R.id.buttonCancel);
//        buttonCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialogEditStatus.cancel();
//            }
//        });
//    }
}