package jannini.android.ciclosp.CustomItemClasses;

import android.os.Parcel;
import android.os.Parcelable;

public class Report implements Parcelable{

	public Double Lat;
	public Double Lng;
	public String Tipo, Descricao, timestamp, Endereco;
	
	public Report (String tipo, String endereco, Double lat, Double lng, String descricao, String timestamp){
		this.Tipo = tipo;
		this.Endereco = endereco;
    	this.Lat = lat;
        this.Lng = lng;
        this.Descricao = descricao;
        this.timestamp= timestamp;
	}
	
	public Report(Parcel in) {
        Tipo = in.readString();
        Endereco = in.readString();
        Lat = in.readDouble();
        Lng = in.readDouble();
        Descricao = in.readString();
        timestamp = in.readString();
	}
	
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(Tipo);
		out.writeString(Endereco);
		out.writeDouble(Lat);
		out.writeDouble(Lng);
		out.writeString(Descricao);
		out.writeString(timestamp);
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public static final Parcelable.Creator<Report> CREATOR = new Parcelable.Creator<Report>() {
        public Report createFromParcel(Parcel in) {
            return new Report(in);
        }

        public Report[] newArray(int size) {
            return new Report[size];
        }
    };
	
}



