import React, {Component} from 'react'
// import TileLayer from "react-leaflet/lib/TileLayer";
import {Map, Marker, Popup, TileLayer} from "react-leaflet";
import 'leaflet/dist/leaflet.css'
import L from 'leaflet';

delete L.Icon.Default.prototype._getIconUrl;

L.Icon.Default.mergeOptions({
    iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
    iconUrl: require('leaflet/dist/images/marker-icon.png'),
    shadowUrl: require('leaflet/dist/images/marker-shadow.png')
});

export default class SimpleExample extends Component {
    state = {
        lat: 51.505,
        lng: -0.09,
        zoom: 13,
    }

    onMapClick(e) {
        L.popup
            .setLatLng(e.latlng)
            .setContent("You clicked the map at " + e.latlng.toString())
        // .openOn(mymap);
    }

    render() {
        const position = [this.state.lat, this.state.lng]
        return (
            <Map center={position}
                 zoom={this.state.zoom}
                 style={{height: '350px'}}
                 onClick={(e) => this.onMapClick(e)}>
                <TileLayer
                    attribution="&amp;copy <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
                    url="https://cartodb-basemaps-{s}.global.ssl.fastly.net/light_all/{z}/{x}/{y}.png"
                />
                <Marker position={position}>
                    <Popup>
                        A pretty CSS3 popup. <br/> Easily customizable. {position}
                    </Popup>
                </Marker>
            </Map>
        )
    }
}
