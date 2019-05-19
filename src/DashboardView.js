import * as React from "react";
import {Line} from "react-chartjs-2";
import Typography from "@material-ui/core/Typography";
import {Paper} from "@material-ui/core";
import ConnectionStateView from "./ConnectionStateView";
import {connect} from "react-redux";

class DashboardView extends React.Component {

    getChartData = () => {
        return {
            labels: this.props.pulses.map(pulse => pulse.toFixed(0)),
            datasets: [
                {
                    label: 'BPM',
                    data: this.props.pulses,
                    backgroundColor: ['#E7E7E7'],
                    borderColor: ['#797979'],
                    borderWidth: 2,
                    fill: true,
                    lineTension: 0.1
                }
            ],
        }
    };

    render() {
        const beat = this.props.pulses && this.props.pulses.length > 0 ? this.props.pulses[0] : 0;
        return (
            <div style={{textAlign: 'center', marginTop: 100}}>
                <Typography variant={"h1"} style={{color: '#797979'}}>{beat}</Typography>

                <Typography variant={"subtitle2"}>Device: {this.props.deviceId}</Typography>
                <ConnectionStateView/>

                <Paper style={{marginTop: 20, marginLeft: 20, marginRight: 20}}>
                    <Line data={this.getChartData()} height={60}/>
                </Paper>
            </div>
        );
    }

}

const mapStateToProps = state => {
    const beats = state.beats ? state.beats : []
    const deviceId = state.deviceId
    const pulses = beats.map(pulse => {
        return pulse.beat
    })
    return {pulses, deviceId}
}

const Dashboard = connect(mapStateToProps)(DashboardView)
export default Dashboard