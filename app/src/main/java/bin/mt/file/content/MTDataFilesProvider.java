//package bin.mt.file.content;
//
//import android.content.Context;
//import android.content.pm.ApplicationInfo;
//import android.content.pm.ProviderInfo;
//import android.database.Cursor;
//import android.database.MatrixCursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.CancellationSignal;
//import android.os.Environment;
//import android.os.ParcelFileDescriptor;
//import android.provider.DocumentsProvider;
//import android.system.ErrnoException;
//import android.system.Os;
//import android.system.StructStat;
//import android.webkit.MimeTypeMap;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.List;
//
//public class MTDataFilesProvider extends DocumentsProvider {
//    public static final String[] f = {"root_id", "mime_types", "flags", "icon", "title", "summary", "document_id"};
//    public static final String[] g = {"document_id", "mime_type", "_display_name", "last_modified", "flags", "_size", "mt_extras"};
//    public String b;
//    public File c;
//    public File d;
//    public File e;
//
//    /* JADX WARN: Removed duplicated region for block: B:17:0x002f  */
//    /*
//        Code decompiled incorrectly, please refer to instructions dump.
//    */
//    public static boolean a(File file) {
//        boolean z;
//        File[] listFiles;
//        if (file.isDirectory()) {
//            try {
//                z = (Os.lstat(file.getPath()).st_mode & 61440) == 4096;
//            } catch (ErrnoException e) {
//                e.printStackTrace();
//                z = false;
//            }
//            listFiles = file.listFiles();
//            if (!z && listFiles!=null) {
//                for (File file2 : listFiles) {
//                    if (!a(file2)) {
//                        return false;
//                    }
//                }
//            }
//
//        }
//        return file.delete();
//    }
//
//    public static String c(File file) {
//        if (file.isDirectory()) {
//            return "vnd.android.document/directory";
//        }
//        String name = file.getName();
//        int lastIndexOf = name.lastIndexOf(46);
//        if (lastIndexOf >= 0) {
//            String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(name.substring(lastIndexOf + 1).toLowerCase());
//            return mimeTypeFromExtension != null ? mimeTypeFromExtension : "application/octet-stream";
//        }
//        return "application/octet-stream";
//    }
//
//    @Override // android.provider.DocumentsProvider, android.content.ContentProvider
//    public final void attachInfo(Context context, ProviderInfo providerInfo) {
//        super.attachInfo(context, providerInfo);
//        this.b = context.getPackageName();
//        this.c = context.getFilesDir().getParentFile();
//        File externalStorageDirectory = Environment.getExternalStorageDirectory();
//        this.d = new File(externalStorageDirectory, "Android/data/" + this.b);
//        File externalStorageDirectory2 = Environment.getExternalStorageDirectory();
//        this.e = new File(externalStorageDirectory2, "Android/obb/" + this.b);
//    }
//
//    public final File b(String str, boolean z) throws FileNotFoundException {
//        String substring;
//        if (str.startsWith(this.b)) {
//            String substring2 = str.substring(this.b.length());
//            if (substring2.startsWith("/")) {
//                substring2 = substring2.substring(1);
//            }
//            File file = null;
//            if (substring2.isEmpty()) {
//                return null;
//            }
//            int indexOf = substring2.indexOf(47);
//            if (indexOf == -1) {
//                substring = "";
//            } else {
//                String substring3 = substring2.substring(0, indexOf);
//                substring = substring2.substring(indexOf + 1);
//                substring2 = substring3;
//            }
//            if (substring2.equalsIgnoreCase("data")) {
//                file = new File(this.c, substring);
//            } else if (substring2.equalsIgnoreCase("android_data")) {
//                file = new File(this.d, substring);
//            } else if (substring2.equalsIgnoreCase("android_obb")) {
//                file = new File(this.e, substring);
//            }
//            if (file == null || (z && !file.exists())) {
//                throw new FileNotFoundException(str.concat(" not found"));
//            }
//            return file;
//        }
//        throw new FileNotFoundException(str.concat(" not found"));
//    }
//
//    /* JADX WARN: Removed duplicated region for block: B:33:0x0074  */
//    /* JADX WARN: Removed duplicated region for block: B:53:0x00c8 A[Catch: Exception -> 0x00dd, TryCatch #1 {Exception -> 0x00dd, blocks: (B:10:0x001d, B:12:0x0031, B:13:0x0036, B:15:0x003e, B:35:0x0078, B:36:0x007f, B:37:0x0083, B:39:0x0089, B:40:0x008d, B:41:0x0093, B:44:0x009f, B:45:0x00a7, B:48:0x00ae, B:49:0x00b4, B:52:0x00c0, B:53:0x00c8, B:56:0x00cf, B:22:0x0053, B:25:0x005d, B:28:0x0067, B:14:0x0039), top: B:63:0x001d, inners: #0, #2 }] */
//    @Override // android.provider.DocumentsProvider, android.content.ContentProvider
//    /*
//        Code decompiled incorrectly, please refer to instructions dump.
//    */
//    public final Bundle call(String str, String str2, Bundle bundle) {
//        String str3;
//        int hashCode = 0;
//        char c;
//        String message = null;
//        Bundle call = super.call(str, str2, bundle);
//        if (call != null) {
//            return call;
//        }
//        if (str.startsWith("mt:")) {
//            Bundle bundle2 = new Bundle();
//            try {
//                List<String> pathSegments = ((Uri) bundle.getParcelable("uri")).getPathSegments();
//                str3 = pathSegments.size() >= 4 ? pathSegments.get(3) : pathSegments.get(1);
//                hashCode = str.hashCode();
//            } catch (Exception e) {
//                bundle2.putBoolean("result", false);
//                bundle2.putString("message", e.toString());
//            }
//            if (hashCode == -1645162251) {
//                if (str.equals("mt:setPermissions")) {
//                    c = 1;
//                    bundle2.putBoolean("result", false);
//                    return bundle2;
//                }
//                c = 65535;
//                bundle2.putBoolean("result", false);
//                return bundle2;
//            } else if (hashCode == 214442514) {
//                if (str.equals("mt:createSymlink")) {
//                    c = 2;
//                    bundle2.putBoolean("result", false);
//                    return bundle2;
//                }
//                c = 65535;
//                bundle2.putBoolean("result", false);
//                return bundle2;
//            } else {
//                if (hashCode == 1713485102 && str.equals("mt:setLastModified")) {
//                    c = 0;
//                    bundle2.putBoolean("result", false);
//                    message = "Unsupported method: ".concat(str);
//                    bundle2.putString("message", message);
//                    return bundle2;
//                }
//                c = 65535;
//                bundle2.putBoolean("result", false);
//                return bundle2;
//            }
//        }
//        return null;
//    }
//
//    @Override // android.provider.DocumentsProvider
//    public final String createDocument(String str, String str2, String str3) throws FileNotFoundException {
//        StringBuilder sb;
//        File b = b(str, true);
//        if (b != null) {
//            File file = new File(b, str3);
//            int i = 2;
//            while (file.exists()) {
//                file = new File(b, str3 + " (" + i + ")");
//                i++;
//            }
//            try {
//                if ("vnd.android.document/directory".equals(str2) ? file.mkdir() : file.createNewFile()) {
//                    if (str.endsWith("/")) {
//                        sb = new StringBuilder();
//                        sb.append(str);
//                        sb.append(file.getName());
//                    } else {
//                        sb = new StringBuilder();
//                        sb.append(str);
//                        sb.append("/");
//                        sb.append(file.getName());
//                    }
//                    str = sb.toString();
//                    return str;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        throw new FileNotFoundException("Failed to create document in " + str + " with name " + str3);
//    }
//
//    public final void d(MatrixCursor matrixCursor, String str, File file) throws FileNotFoundException {
//        int i;
//        String name;
//        if (file == null) {
//            file = b(str, true);
//        }
//        boolean z = false;
//        if (file == null) {
//            MatrixCursor.RowBuilder newRow = matrixCursor.newRow();
//            newRow.add("document_id", this.b);
//            newRow.add("_display_name", this.b);
//            newRow.add("_size", 0L);
//            newRow.add("mime_type", "vnd.android.document/directory");
//            newRow.add("last_modified", 0);
//            newRow.add("flags", 0);
//            return;
//        }
//        if (file.isDirectory()) {
//            if (file.canWrite()) {
//                i = 8;
//            }
//            i = 0;
//        } else {
//            if (file.canWrite()) {
//                i = 2;
//            }
//            i = 0;
//        }
//        if (file.getParentFile().canWrite()) {
//            i = i | 4 | 64;
//        }
//        String path = file.getPath();
//        if (path.equals(this.c.getPath())) {
//            name = "data";
//        } else if (path.equals(this.d.getPath())) {
//            name = "android_data";
//        } else if (path.equals(this.e.getPath())) {
//            name = "android_obb";
//        } else {
//            name = file.getName();
//            z = true;
//        }
//        MatrixCursor.RowBuilder newRow2 = matrixCursor.newRow();
//        newRow2.add("document_id", str);
//        newRow2.add("_display_name", name);
//        newRow2.add("_size", Long.valueOf(file.length()));
//        newRow2.add("mime_type", c(file));
//        newRow2.add("last_modified", Long.valueOf(file.lastModified()));
//        newRow2.add("flags", Integer.valueOf(i));
//        newRow2.add("mt_path", file.getAbsolutePath());
//        if (z) {
//            try {
//                StringBuilder sb = new StringBuilder();
//                StructStat lstat = Os.lstat(path);
//                sb.append(lstat.st_mode);
//                sb.append("|");
//                sb.append(lstat.st_uid);
//                sb.append("|");
//                sb.append(lstat.st_gid);
//                if ((lstat.st_mode & 61440) == 40960) {
//                    sb.append("|");
//                    sb.append(Os.readlink(path));
//                }
//                newRow2.add("mt_extras", sb.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override // android.provider.DocumentsProvider
//    public final void deleteDocument(String str) throws FileNotFoundException {
//        File b = b(str, true);
//        if (b == null || !a(b)) {
//            throw new FileNotFoundException("Failed to delete document ".concat(str));
//        }
//    }
//
//    @Override // android.provider.DocumentsProvider
//    public final String getDocumentType(String str) throws FileNotFoundException {
//        File b = b(str, true);
//        return b == null ? "vnd.android.document/directory" : c(b);
//    }
//
//    @Override // android.provider.DocumentsProvider
//    public final boolean isChildDocument(String str, String str2) {
//        return str2.startsWith(str);
//    }
//
//    @Override // android.provider.DocumentsProvider
//    public final String moveDocument(String str, String str2, String str3) throws FileNotFoundException {
//        File b = b(str, true);
//        File b2 = b(str3, true);
//        if (b != null && b2 != null) {
//            File file = new File(b2, b.getName());
//            if (!file.exists() && b.renameTo(file)) {
//                if (str3.endsWith("/")) {
//                    return str3 + file.getName();
//                }
//                return str3 + "/" + file.getName();
//            }
//        }
//        throw new FileNotFoundException("Filed to move document " + str + " to " + str3);
//    }
//
//    @Override // android.content.ContentProvider
//    public final boolean onCreate() {
//        return true;
//    }
//
//    @Override // android.provider.DocumentsProvider
//    public final ParcelFileDescriptor openDocument(String str, String str2, CancellationSignal cancellationSignal) throws FileNotFoundException {
//        File b = b(str, false);
//        if (b != null) {
//            return ParcelFileDescriptor.open(b, ParcelFileDescriptor.parseMode(str2));
//        }
//        throw new FileNotFoundException(str.concat(" not found"));
//    }
//
//    @Override // android.provider.DocumentsProvider
//    public final Cursor queryChildDocuments(String str, String[] strArr, String str2) throws FileNotFoundException {
//        if (str.endsWith("/")) {
//            str = str.substring(0, str.length() - 1);
//        }
//        if (strArr == null) {
//            strArr = g;
//        }
//        MatrixCursor matrixCursor = new MatrixCursor(strArr);
//        File b = b(str, true);
//        if (b == null) {
//            d(matrixCursor, str.concat("/data"), this.c);
//            if (this.d.exists()) {
//                d(matrixCursor, str.concat("/android_data"), this.d);
//            }
//            if (this.e.exists()) {
//                d(matrixCursor, str.concat("/android_obb"), this.e);
//            }
//        } else {
//            File[] listFiles = b.listFiles();
//            if (listFiles != null) {
//                for (File file : listFiles) {
//                    d(matrixCursor, str + "/" + file.getName(), file);
//                }
//            }
//        }
//        return matrixCursor;
//    }
//
//    @Override // android.provider.DocumentsProvider
//    public final Cursor queryDocument(String str, String[] strArr) throws FileNotFoundException {
//        if (strArr == null) {
//            strArr = g;
//        }
//        MatrixCursor matrixCursor = new MatrixCursor(strArr);
//        d(matrixCursor, str, null);
//        return matrixCursor;
//    }
//
//    @Override // android.provider.DocumentsProvider
//    public final Cursor queryRoots(String[] strArr) {
//        ApplicationInfo applicationInfo = getContext().getApplicationInfo();
//        String charSequence = applicationInfo.loadLabel(getContext().getPackageManager()).toString();
//        if (strArr == null) {
//            strArr = f;
//        }
//        MatrixCursor matrixCursor = new MatrixCursor(strArr);
//        MatrixCursor.RowBuilder newRow = matrixCursor.newRow();
//        newRow.add("root_id", this.b);
//        newRow.add("document_id", this.b);
//        newRow.add("summary", this.b);
//        newRow.add("flags", 17);
//        newRow.add("title", charSequence);
//        newRow.add("mime_types", "*/*");
//        newRow.add("icon", Integer.valueOf(applicationInfo.icon));
//        return matrixCursor;
//    }
//
//    @Override // android.provider.DocumentsProvider
//    public final void removeDocument(String str, String str2) throws FileNotFoundException {
//        deleteDocument(str);
//    }
//
//    @Override // android.provider.DocumentsProvider
//    public final String renameDocument(String str, String str2) throws FileNotFoundException {
//        File b = b(str, true);
//        if (b == null || !b.renameTo(new File(b.getParentFile(), str2))) {
//            throw new FileNotFoundException("Failed to rename document " + str + " to " + str2);
//        }
//        int lastIndexOf = str.lastIndexOf(47, str.length() - 2);
//        return str.substring(0, lastIndexOf) + "/" + str2;
//    }
//}