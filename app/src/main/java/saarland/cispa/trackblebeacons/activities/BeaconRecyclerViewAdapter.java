package saarland.cispa.trackblebeacons.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import saarland.cispa.bletrackerlib.data.SimpleBeacon;
import saarland.cispa.bletrackerlib.parser.DateParser;
import saarland.cispa.trackblebeacons.R;
import saarland.cispa.trackblebeacons.helpers.CountHelper;

import static saarland.cispa.bletrackerlib.data.SimpleBeaconLayouts.ALTBEACON_LAYOUT;
import static saarland.cispa.bletrackerlib.data.SimpleBeaconLayouts.EDDYSTONE_TLM_LAYOUT;
import static saarland.cispa.bletrackerlib.data.SimpleBeaconLayouts.EDDYSTONE_UID_LAYOUT;
import static saarland.cispa.bletrackerlib.data.SimpleBeaconLayouts.EDDYSTONE_URL_LAYOUT;
import static saarland.cispa.bletrackerlib.data.SimpleBeaconLayouts.IBEACON_LAYOUT;
import static saarland.cispa.bletrackerlib.data.SimpleBeaconLayouts.RUUVI_LAYOUT;

/**
 * Displays the beacons in Nearby and Scan view based on the data in the correct tile
 */
public class BeaconRecyclerViewAdapter extends ListAdapter<SimpleBeacon, BeaconRecyclerViewAdapter.BaseHolder> {

    private OnControlsOpen onControlsOpen;
    private Context context;

    private static final DiffUtil.ItemCallback<SimpleBeacon> DIFF_CALLBACK = new DiffUtil.ItemCallback<SimpleBeacon>() {
        @Override
        public boolean areItemsTheSame(@NonNull SimpleBeacon oldItem, @NonNull SimpleBeacon newItem) {
            return oldItem.hashcode == newItem.hashcode;
        }

        @Override
        public boolean areContentsTheSame(@NonNull SimpleBeacon oldItem, @NonNull SimpleBeacon newItem) {
            return oldItem == newItem;
        }
    };


    public BeaconRecyclerViewAdapter(Context context, OnControlsOpen onControlsOpen) {
        super(DIFF_CALLBACK);

        this.context = context;
        this.onControlsOpen = onControlsOpen;
    }

    @NonNull
    @Override
    public BaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_item, parent, false);

        switch (viewType) {
            case R.layout.eddystone_uid_item: return new EddystoneUidHolder(view);
            case R.layout.eddystone_url_item: return new EddystoneUrlHolder(view);
            case R.layout.ruuvitag_item: return new RuuviTagHolder(view);
            case R.layout.ibeacon_altbeacon_item: return new AltBeaconIbeaconHolder(view);
            default: return new BaseHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseHolder holder, int position) {
        SimpleBeacon beacon = getItem(position);

        holder.bindView(beacon);
    }

    @Override
    public int getItemViewType(int position) {
        String beaconType = getItem(position).beaconType;

        if (EDDYSTONE_UID_LAYOUT.name().equals(beaconType)) {
            return R.layout.eddystone_uid_item;
        } else if (EDDYSTONE_URL_LAYOUT.name().equals(beaconType)) {
            return R.layout.eddystone_url_item;
        } else if (RUUVI_LAYOUT.name().equals(beaconType)) {
            return R.layout.ruuvitag_item;
        } else if (ALTBEACON_LAYOUT.name().equals(beaconType) || IBEACON_LAYOUT.name().equals(beaconType)) {
            return R.layout.ibeacon_altbeacon_item;
        } else {
            return R.layout.ibeacon_altbeacon_item;
        }
    }

    static class BaseHolder extends RecyclerView.ViewHolder {

        SimpleBeacon displayedBeacon;
        Context context;

        Button visit_website_btn;
        Button ruuvi_visit_website_button;
        CardView card;
        TextView address;
        TextView distance;
        TextView manufacturer;
        TextView last_seen;
        TextView rssid;
        TextView tx;

        TextView battery;
        TextView temperature;
        TextView uptime;
        TextView pdu_sent;

        LinearLayout battery_container;
        LinearLayout temperature_container;
        LinearLayout uptime_container;
        LinearLayout pdu_sent_container;

        ConstraintLayout ibeacon_altbeacon_item;
        ConstraintLayout eddystone_uid_item;
        ConstraintLayout eddystone_url_item;
        ConstraintLayout ruuvitag_item;

        BaseHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();

            visit_website_btn = itemView.findViewById(R.id.visit_website_btn);
            ruuvi_visit_website_button = itemView.findViewById(R.id.ruuvi_visit_website_btn);
            card = itemView.findViewById(R.id.card);
            address = itemView.findViewById(R.id.address);
            distance = itemView.findViewById(R.id.distance);
            manufacturer = itemView.findViewById(R.id.manufacturer);
            last_seen = itemView.findViewById(R.id.last_seen);
            rssid = itemView.findViewById(R.id.rssi);
            tx = itemView.findViewById(R.id.tx);

            battery = itemView.findViewById(R.id.battery);
            temperature = itemView.findViewById(R.id.temperature);
            uptime = itemView.findViewById(R.id.uptime);
            pdu_sent = itemView.findViewById(R.id.pdu_sent);

            battery_container = itemView.findViewById(R.id.battery_container);
            temperature_container = itemView.findViewById(R.id.temperature_container);
            uptime_container = itemView.findViewById(R.id.uptime_container);
            pdu_sent_container = itemView.findViewById(R.id.pdu_sent_container);

            ibeacon_altbeacon_item = itemView.findViewById(R.id.ibeacon_altbeacon_item);
            eddystone_uid_item = itemView.findViewById(R.id.eddystone_uid_item);
            eddystone_url_item = itemView.findViewById(R.id.eddystone_url_item);
            ruuvitag_item = itemView.findViewById(R.id.ruuvitag_item);
        }


        public void bindView(SimpleBeacon beacon) {
            displayedBeacon = beacon;

            visit_website_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onUrlClicked();
                }
            });
            ruuvi_visit_website_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onUrlClicked();
                }
            });
            card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
            address.setText(beacon.bluetoothAddress);
            if (beacon.distance >= 0) {
                distance.setText(String.format(Locale.getDefault(), "%.2f", beacon.distance));
            } else {
                distance.setText(context.getString(R.string.no_distance_available));
            }
            manufacturer.setText(String.format(Locale.getDefault(), "0x%04X", beacon.manufacturer));

            last_seen.setText(DateParser.makeDisplayDate(DateParser.utcToLocalDate(itemView.getContext() ,beacon.timestamp)));

            rssid.setText(String.format(itemView.getContext().getString(R.string.rssi_x_dbm), beacon.signalStrength));
            tx.setText(String.format(itemView.getContext().getString(R.string.tx_x_dbm), beacon.transmitPower));


            if (EDDYSTONE_TLM_LAYOUT.name().equals(beacon.beaconType)) {
                setContainersVisible(true);

                SimpleBeacon.Telemetry telemetry = beacon.telemetry;
                if (telemetry != null) {
                    battery.setText(String.format(itemView.getContext().getString(R.string.battery_x_mv), telemetry.batteryMilliVolts));
                    temperature.setText(String.format(itemView.getContext().getString(R.string.temperature_x_degres), telemetry.temperature));
                    uptime.setText(String.format(itemView.getContext().getString(R.string.uptime_x_seconds), CountHelper.coolFormat(telemetry.uptime, 0)));
                    pdu_sent.setText(String.format(itemView.getContext().getString(R.string.x_packets_sent), CountHelper.coolFormat(telemetry.pduCount, 0)));
                }
            } else {
                setContainersVisible(false);
            }
        }
        private void setContainersVisible(boolean visible) {
            int visibility = visible ? View.VISIBLE : View.GONE;
            battery_container.setVisibility(visibility);
            temperature_container.setVisibility(visibility);
            uptime_container.setVisibility(visibility);
            pdu_sent_container.setVisibility(visibility);
        }

        void hideAllLayouts() {
            ibeacon_altbeacon_item.setVisibility(View.GONE);
            eddystone_uid_item.setVisibility(View.GONE);
            eddystone_url_item.setVisibility(View.GONE);
            ruuvitag_item.setVisibility(View.GONE);
        }

        private void onUrlClicked() {
            if (displayedBeacon != null && displayedBeacon.eddystoneUrlData != null) {
                String url = displayedBeacon.eddystoneUrlData.url;
                try {
                    Uri uri = Uri.parse(url);
                    context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (Exception e) {

                }
            }
        }
    }

    static class AltBeaconIbeaconHolder extends BaseHolder {

        TextView beacon_type;
        TextView url;
        TextView proximity_uuid;
        TextView major;
        TextView minor;

        AltBeaconIbeaconHolder(@NonNull View itemView) {
            super(itemView);

            beacon_type = itemView.findViewById(R.id.beacon_type);
            proximity_uuid = itemView.findViewById(R.id.proximity_uuid);
            major = itemView.findViewById(R.id.major);
            minor = itemView.findViewById(R.id.minor);
        }

        @Override
        public void bindView(SimpleBeacon beacon) {
            super.bindView(beacon);

            card.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.ibeaconBackground));

            hideAllLayouts();
            ibeacon_altbeacon_item.setVisibility(View.VISIBLE);

            beacon_type.setText(String.format(Locale.getDefault(), "%s",
                    (IBEACON_LAYOUT.name().equals(beacon.beaconType) ? itemView.getContext().getString(R.string.ibeacon) : itemView.getContext().getString(R.string.altbeacon))));

            SimpleBeacon.AltbeaconIBeaconData altIData = beacon.altbeaconIBeaconData;

            if (altIData != null) {
                proximity_uuid.setText(String.format(itemView.getContext().getString(R.string.uuid_x), altIData.uuid));
                major.setText(String.format(itemView.getContext().getString(R.string.major_x), altIData.major));
                minor.setText(String.format(itemView.getContext().getString(R.string.minor_x), altIData.minor));
            }
        }
    }

    static class EddystoneUidHolder extends BaseHolder {

        TextView beacon_type;
        TextView namespace_id;
        TextView instance_id;

        EddystoneUidHolder(@NonNull View itemView) {
            super(itemView);

            beacon_type = itemView.findViewById(R.id.beacon_type);
            namespace_id = itemView.findViewById(R.id.namespace_id);
            instance_id = itemView.findViewById(R.id.instance_id);
        }

        @Override
        public void bindView(SimpleBeacon beacon) {
            super.bindView(beacon);

            card.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.eddystoneUidBackground));

            hideAllLayouts();
            eddystone_uid_item.setVisibility(View.VISIBLE);

            beacon_type.setText(String.format(Locale.getDefault(), "%s%s",
                    itemView.getContext().getString(R.string.eddystone_uid),
            (beacon.telemetry != null ? itemView.getContext().getString(R.string.plus_tlm) : "")));

            SimpleBeacon.EddystoneUID eddystoneUID = beacon.eddystoneUidData;
            if (eddystoneUID != null) {
                namespace_id.setText(String.format(itemView.getContext().getString(R.string.namespace_id_x), eddystoneUID.instanceId));
                instance_id.setText(String.format(itemView.getContext().getString(R.string.instance_id_x), eddystoneUID.namespaceId));
            }
        }
    }

    static class EddystoneUrlHolder extends BaseHolder {

        TextView beacon_type;
        TextView url;

        EddystoneUrlHolder(@NonNull View itemView) {
            super(itemView);

            beacon_type = itemView.findViewById(R.id.beacon_type);
            url = itemView.findViewById(R.id.url);
        }

        @Override
        public void bindView(SimpleBeacon beacon) {
            super.bindView(beacon);

            card.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.eddystoneUrlBackground));

            hideAllLayouts();
            eddystone_url_item.setVisibility(View.VISIBLE);

            beacon_type.setText(String.format(Locale.getDefault(), "%s%s",
                    itemView.getContext().getString(R.string.eddystone_url),
            (beacon.telemetry != null ? itemView.getContext().getString(R.string.plus_tlm) : "")));

            if (beacon.eddystoneUrlData != null) {
                url.setText(String.format(itemView.getContext().getString(R.string.url_x), beacon.eddystoneUrlData.url));
            }
        }
    }

    static class RuuviTagHolder extends BaseHolder {

        TextView beacon_type;
        TextView ruuvi_url;
        TextView ruuvi_air_pressure;
        TextView ruuvi_temperature;
        TextView ruuvi_humidity;

        RuuviTagHolder(@NonNull View itemView) {
            super(itemView);

            beacon_type = itemView.findViewById(R.id.beacon_type);
            ruuvi_url = itemView.findViewById(R.id.ruuvi_url);
            ruuvi_air_pressure = itemView.findViewById(R.id.ruuvi_air_pressure);
            ruuvi_temperature = itemView.findViewById(R.id.ruuvi_temperature);
            ruuvi_humidity = itemView.findViewById(R.id.ruuvi_humidity);
        }

        @Override
        public void bindView(SimpleBeacon beacon) {
            super.bindView(beacon);

            card.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.ruuvitagBackground));

            hideAllLayouts();
            ruuvitag_item.setVisibility(View.VISIBLE);

            beacon_type.setText(String.format(Locale.getDefault(), "%s", itemView.getContext().getString(R.string.ruuvitag)));

            if (beacon.eddystoneUrlData != null) {
                ruuvi_url.setText(String.format(itemView.getContext().getString(R.string.url_x), beacon.eddystoneUrlData.url));
            }
            SimpleBeacon.Ruuvi ruuvi = beacon.ruuvi;
            if (ruuvi != null) {
                ruuvi_air_pressure.setText(String.format(itemView.getContext().getString(R.string.air_pressure_x_hpa), ruuvi.airPressure));
                ruuvi_temperature.setText(String.format(itemView.getContext().getString(R.string.temperature_d_degres), ruuvi.temperature));
                ruuvi_humidity.setText(String.format(itemView.getContext().getString(R.string.humidity_x_percent), ruuvi.humidity));
            }
        }
    }


    public interface OnControlsOpen {
        void onControlsOpen(SimpleBeacon beacon);
    }
}
