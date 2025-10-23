package co.edu.unipiloto.appbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class ShipmentActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "encomienda_channel";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Crear canal de notificación (obligatorio en Android 8+)
        crearCanalNotificacion();

        // Botón para compartir formulario
        Button btnCompartir = findViewById(R.id.btnCompartir);
        btnCompartir.setOnClickListener(v -> {
            String mensaje = "Nueva solicitud de envío:\n" +
                    "Remitente: Juan Pérez\n" +
                    "Destinatario: Ana Torres\n" +
                    "Dirección: Calle 123 #45-67\n" +
                    "Peso: 5 kg";

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Formulario de envío");
            intent.putExtra(Intent.EXTRA_TEXT, mensaje);

            startActivity(Intent.createChooser(intent, "Compartir formulario con..."));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shipment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_characteristics) {
            Intent intent = new Intent(this, CharacteristicsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_notification) {
            mostrarNotificacion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // 🔔 Método para mostrar una notificación
    private void mostrarNotificacion() {
        // Verificar permiso (solo Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Nueva Encomienda")
                .setContentText("Tu solicitud de envío fue registrada exitosamente.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        Toast.makeText(this, "Notificación creada", Toast.LENGTH_SHORT).show();
    }

    // 🔧 Crear canal para las notificaciones
    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Canal Encomiendas";
            String description = "Canal para notificaciones de envíos";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
