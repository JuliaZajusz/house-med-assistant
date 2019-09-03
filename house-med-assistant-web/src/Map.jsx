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
        markers: [[51.505, -0.09]]
    }

    onMapClick(e) {
        L.popup
            .setLatLng(e.latlng)
            .setContent("You clicked the map at " + e.latlng.toString())
        // .openOn(mymap);
    }

    addMarker = (e) => {
        const {markers} = this.state
        let lat = Math.round(e.latlng.lat * 100) / 100
        let lng = Math.round(e.latlng.lng * 100) / 100
        markers.push([lat, lng])
        this.setState({markers})
    }

    render() {
        const startPosition = [this.state.lat, this.state.lng]
        return (
            <Map center={startPosition}
                 zoom={this.state.zoom}
                 style={{height: '350px'}}
                 onClick={this.addMarker}
                // onClick={(e) => this.onMapClick(e)}
            >
                <TileLayer
                    attribution="&amp;copy <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
                    url="https://cartodb-basemaps-{s}.global.ssl.fastly.net/light_all/{z}/{x}/{y}.png"
                />
                {this.state.markers.map((markerPosition, idx) =>
                    <Marker key={`marker-${idx}`} position={markerPosition} onClick={() => console.log("a")}>
                        <Popup key={`popup-${idx}`}>
                            <span>
                                A pretty CSS3 popup. <br/>
                                Easily customizable.<br/>
                                {markerPosition[0]}, {markerPosition[1]}
                            </span>
                        </Popup>
                    </Marker>
                )}
            </Map>
        )
    }
}
