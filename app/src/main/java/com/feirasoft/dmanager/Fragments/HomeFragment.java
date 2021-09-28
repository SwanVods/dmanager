package com.feirasoft.dmanager.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feirasoft.dmanager.FileAdapter;
import com.feirasoft.dmanager.FileOpener;
import com.feirasoft.dmanager.OnSelectedFileListener;
import com.feirasoft.dmanager.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment implements OnSelectedFileListener {
    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<File> fileList;
    private LinearLayout linearImage, linearVideo, linearMusic, linearDocs, linearDownload, linearApk;


    File storage;
    String data;
    String[] items = {"Detail", "Ubah nama", "Hapus", "Bagikan"};

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        linearImage = view.findViewById(R.id.linearImage);
        linearVideo = view.findViewById(R.id.linearVideo);
        linearMusic = view.findViewById(R.id.linearMusic);
        linearDocs = view.findViewById(R.id.linearDocs);
        linearDownload = view.findViewById(R.id.linearDownload);
        linearApk = view.findViewById(R.id.linearApk);

        linearImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display image files
                Bundle args = new Bundle();
                args.putString("fileType", "image");
                CategorizedFragment categorizedFragment = new CategorizedFragment();
                categorizedFragment.setArguments(args);

                getFragmentManager().beginTransaction().add(R.id.fragment_container, categorizedFragment).addToBackStack(null).commit();
            }
        });
        linearMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("fileType", "music");
                CategorizedFragment categorizedFragment = new CategorizedFragment();
                categorizedFragment.setArguments(args);

                getFragmentManager().beginTransaction().add(R.id.fragment_container, categorizedFragment).addToBackStack(null).commit();
            }
        });
        linearDocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("fileType", "docs");
                CategorizedFragment categorizedFragment = new CategorizedFragment();
                categorizedFragment.setArguments(args);

                getFragmentManager().beginTransaction().add(R.id.fragment_container, categorizedFragment).addToBackStack(null).commit();
            }
        });
        linearDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("fileType", "downloads");
                CategorizedFragment categorizedFragment = new CategorizedFragment();
                categorizedFragment.setArguments(args);

                getFragmentManager().beginTransaction().add(R.id.fragment_container, categorizedFragment).addToBackStack(null).commit();
            }
        });
        linearApk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("fileType", "apk");
                CategorizedFragment categorizedFragment = new CategorizedFragment();
                categorizedFragment.setArguments(args);

                getFragmentManager().beginTransaction().add(R.id.fragment_container, categorizedFragment).addToBackStack(null).commit();
            }
        });

        runtimePermission();

        return view;
    }
    private void runtimePermission() {
        Dexter.withContext(getContext()).withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                displayFiles();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public ArrayList<File> findFiles(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for (File singleFile : files) {
            if(singleFile.isDirectory() && !singleFile.isHidden()) {
                arrayList.addAll(findFiles(singleFile));
            } else if (singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".jpg") ||
                    singleFile.getName().toLowerCase().endsWith(".png") || singleFile.getName().toLowerCase().endsWith(".mp3") ||
                    singleFile.getName().toLowerCase().endsWith(".wav") || singleFile.getName().toLowerCase().endsWith(".mp4") ||
                    singleFile.getName().toLowerCase().endsWith(".pdf") || singleFile.getName().toLowerCase().endsWith(".doc") ||
                    singleFile.getName().toLowerCase().endsWith(".apk") || singleFile.getName().toLowerCase().endsWith(".txt")) {
                arrayList.add(singleFile);
            }
        }

        arrayList.sort(Comparator.comparing(File::lastModified).reversed());
        return arrayList;

    }

    private void displayFiles() {
        recyclerView = view.findViewById(R.id.recycler_recents);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        fileList = new ArrayList<>();
        fileList.addAll(findFiles(Environment.getExternalStorageDirectory()));
        fileAdapter = new FileAdapter(getContext(), fileList, this);
        recyclerView.setAdapter(fileAdapter);
    }

    @Override
    public void onFileClicked(File file) {
        if(file.isDirectory()) {
            Bundle bundle = new Bundle();
            bundle.putString("path", file.getAbsolutePath());
            InternalFragment internalFragment = new InternalFragment();
            internalFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, internalFragment).addToBackStack(null).commit();

        } else {
            try {
                FileOpener.openFile(getContext(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFileLongClicked(File file, int position) {
        final Dialog optionDialog = new Dialog(getContext());
        optionDialog.setContentView(R.layout.option_dialog);
        optionDialog.setTitle("Pilih Opsi");
        ListView option = (ListView) optionDialog.findViewById(R.id.list);
        CustomAdapter customAdapter = new CustomAdapter();
        option.setAdapter(customAdapter);
        optionDialog.show();

        option.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();

                switch (selectedItem) {
                    case "Detail" :
                        AlertDialog.Builder detailDialog = new AlertDialog.Builder(getContext());
                        detailDialog.setTitle("Detail:");
                        final TextView details = new TextView(getContext());
                        detailDialog.setView(details);
                        Date lastModified= new Date(file.lastModified());
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm::ss");
                        String formattedDate = formatter.format(lastModified);

                        details.setText("Nama file : " + file.getName() + "\n" +
                                "Ukuran : " + Formatter.formatShortFileSize(getContext(), file.length()) + "\n" +
                                "Path : " + file.getAbsolutePath() + "\n" +
                                "Terakhir diubah : " + formattedDate);

                        detailDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                optionDialog.cancel();
                            }
                        });
                        AlertDialog alertdialog_details = detailDialog.create();
                        alertdialog_details.show();
                        break;

                    case "Ubah nama" :
                        AlertDialog.Builder renameDialog = new AlertDialog.Builder(getContext());
                        renameDialog.setTitle("Ubah nama file : ");
                        final EditText name = new EditText(getContext());
                        renameDialog.setView(name);

                        renameDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String new_name = name.getEditableText().toString();
                                String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                                File current = new File(file.getAbsolutePath());
                                File destination = new File(file.getAbsolutePath().replace(file.getName(), new_name) + extension);
                                if(current.renameTo(destination)){
                                    fileList.set(position, destination);
                                    fileAdapter.notifyItemChanged(position);
                                    Toast.makeText(getContext(), "Nama berhasil diubah!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Nama gagal diubah!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        renameDialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                optionDialog.cancel();
                            }
                        });

                        AlertDialog alertdialog_rename = renameDialog.create();
                        alertdialog_rename.show();
                        break;

                    case "Hapus" :
                        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getContext());
                        deleteDialog.setTitle("Hapus" + file.getName() + "?");
                        deleteDialog.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                file.delete();
                                fileList.remove(position);
                                fileAdapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "File berhasil dihapus!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        AlertDialog alertdialog_delete = deleteDialog.create();
                        alertdialog_delete.show();
                        break;

                    case "Bagikan" :
                        String fileName = file.getName();
                        Intent share = new Intent();
                        share.setAction(Intent.ACTION_SEND);
                        share.setType("image/jpeg");
                        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        startActivity(Intent.createChooser(share, "Bagikan " + fileName));
                        break;
                }
            }
        });
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return items[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView = getLayoutInflater().inflate(R.layout.option_layout, null);
            TextView txtOption = myView.findViewById(R.id.txtOption);
            ImageView imgOption = myView.findViewById(R.id.imgOption);
            txtOption.setText(items[i]);
            if(items[i].equals("Detail")) {
                imgOption.setImageResource(R.drawable.ic_details);
            } else if(items[i].equals("Ubah nama")) {
                imgOption.setImageResource(R.drawable.ic_rename);
            } else if(items[i].equals("Hapus")) {
                imgOption.setImageResource(R.drawable.ic_delete);
            } else if(items[i].equals("Bagikan")) {
                imgOption.setImageResource(R.drawable.ic_share);
            }
            return myView;
        }
    }
}
