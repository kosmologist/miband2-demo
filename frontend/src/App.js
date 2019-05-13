import React from 'react';
import {initFirebase} from "./fire";
import DashboardView from "./DashboardView";
import {Grid} from "@material-ui/core";
import DebugView from "./DebugView";

class App extends React.Component {

    constructor(props) {
        super(props);
        initFirebase();
    }

    render() {
        return (
            <Grid container>
                <Grid item xs={8}>
                    <DashboardView/>
                </Grid>
                <Grid item xs={4}>
                    <DebugView/>
                </Grid>
            </Grid>
        )
    }
}

export default App;
