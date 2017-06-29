package jannini.android.ciclosp.Models;

import android.content.Context;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.R;

import static jannini.android.ciclosp.Constant.MARKER_TAG_SHARING_STATION;
import static jannini.android.ciclosp.Utils.newline;

public class SharingStation extends MarkerMapElement {
	private int number, bikes, size;
	private String system, name, info, status1, status2;

	public SharingStation(Context context, String system, int number, String name, String info, Double lat, Double lng, String status1, String status2, int bikes, int size){
		super(context, new LatLng (lat, lng), new String[]{MARKER_TAG_SHARING_STATION});
		if (context != null && system != null && name != null && status1 != null && status2 != null) {
			this.context = context;
			this.system = system;
			this.number = number;
			this.name = name;
			this.info = info;
			this.status1 = status1;
			this.status2 = status2;
			this.bikes = bikes;
			this.size = size;
		} else {
			throw new IllegalArgumentException("SharingStation constructor: required parameter is null");
		}
	}

	@Override
	BitmapDescriptor getIcon() {

		int vagasLivres = size - bikes;

		if (status1.equals("A") && status2.equals("EO")) {

			if (bikes == 0) {
				if (system.equals(Constant.SHARING_STATIONS_SYSTEM_BIKE_SAMPA)) {
					return BitmapDescriptorFactory.fromResource(R.drawable.mapic_bs_vazia);
				} else {
					return BitmapDescriptorFactory.fromResource(R.drawable.mapic_cs_vazia);
				}
			} else if (vagasLivres == 0) {
				if (system.equals(Constant.SHARING_STATIONS_SYSTEM_BIKE_SAMPA)) {
					return BitmapDescriptorFactory.fromResource(R.drawable.mapic_bs_cheia);
				} else {
					return BitmapDescriptorFactory.fromResource(R.drawable.mapic_cs_cheia);
				}
			} else {
				if (system.equals(Constant.SHARING_STATIONS_SYSTEM_BIKE_SAMPA)) {
					return BitmapDescriptorFactory.fromResource(R.drawable.mapic_bs_operando);
				} else {
					return BitmapDescriptorFactory.fromResource(R.drawable.mapic_cs_operando);
				}
			}

		} else if (status1.equals("I") && status2.equals("EO")) {
			if (system.equals(Constant.SHARING_STATIONS_SYSTEM_BIKE_SAMPA)) {
				return BitmapDescriptorFactory.fromResource(R.drawable.mapic_bs_offline);
			} else {
				return BitmapDescriptorFactory.fromResource(R.drawable.mapic_cs_offline);
			}
		} else {
			if (system.equals(Constant.SHARING_STATIONS_SYSTEM_BIKE_SAMPA)) {
				return BitmapDescriptorFactory.fromResource(R.drawable.mapic_bs_manutencao);
			} else {
				return BitmapDescriptorFactory.fromResource(R.drawable.mapic_cs_manutencao);
			}
		}
	}

	@Override
	String getTitle() {
		return context.getString(R.string.estacao_bike_sampa) + newline + number + " - " + name;
	}

	@Override
	String getDescription() {

		String description;

		String updateTime = context.getSharedPreferences(Constant.SPKEY_SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constant.SPKEY_SHARING_STATIONS_UPDATE_TIME, "outdated");

		int vagasLivres = size - bikes;

		if (status1.equals("A") && status2.equals("EO")) {

			if (bikes == 0) {
				description = context.getString(R.string.em_operacao)
						+ newline + newline + info
						+ newline + context.getString(R.string.bikes_disponiveis) + " " + bikes
						+ newline + context.getString(R.string.vagas_livres) + " " + vagasLivres
						+ newline + newline + context.getString(R.string.atualizado_as) + " " + updateTime;
			} else if (vagasLivres == 0) {
				description = context.getString(R.string.em_operacao)
						+ newline + newline + info
						+ newline + context.getString(R.string.bikes_disponiveis) + " " + bikes
						+ newline + context.getString(R.string.vagas_livres) + " " + vagasLivres
						+ newline + newline + context.getString(R.string.atualizado_as) + " " + updateTime;

			} else {
				description = context.getString(R.string.em_operacao)
						+ newline + newline + info
						+ newline + context.getString(R.string.bikes_disponiveis) + " " + bikes
						+ newline + context.getString(R.string.vagas_livres) + " " + vagasLivres
						+ newline + newline + context.getString(R.string.atualizado_as) + " " + updateTime;
			}
		} else if (status1.equals("I") && status2.equals("EO")) {
			description = context.getString(R.string.offline)
					+ newline + newline + info
					+ newline + context.getString(R.string.bikes_disponiveis) + " " + bikes
					+ newline + context.getString(R.string.vagas_livres) + " " + vagasLivres
					+ newline + newline + context.getString(R.string.atualizado_as) + " " + updateTime;
		} else {
			description = context.getString(R.string.em_manutencao_implantacao)
					+ newline + newline + info
					+ newline + context.getString(R.string.bikes_disponiveis) + " " + bikes
					+ newline + context.getString(R.string.vagas_livres) + " " + vagasLivres
					+ newline + newline + context.getString(R.string.atualizado_as) + " " + updateTime;
		}

		return description;
	}

}
