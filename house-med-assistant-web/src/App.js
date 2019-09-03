import React, {useState} from 'react';
import './App.css';
import SimpleExample from "./Map";
import {getSalesmanSet} from "./services/SalesmanSetService";
import Button from '@material-ui/core/Button';

function App() {

    const [state, setState] = useState(() => {
            return null
        }
    );


    const showSet = () => {
        console.log("odbywa się łądowanie")
        setState(getSalesmanSet())
    }


    return (
        <div className="App">
            ala
            <Button onClick={() => showSet()}>Show set</Button>
            <SimpleExample data={state}/>
        </div>
)
    ;
}

export default App;
