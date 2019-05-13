import * as React from "react";
import {store} from "./store/DataStore";
import {Line} from "react-chartjs-2";
import Typography from "@material-ui/core/Typography";
import {Paper} from "@material-ui/core";

export default class DashboardView extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            pulses: []
        }

        store.subscribe(() => {
            const pulses =store.getState().beats.map(pulse=>{
                return pulse.beat
            })
            console.log('Pulse length: ' + pulses)
            this.setState({pulses})
        })


        /*        setInterval(() => {
                    let {pulses} = this.state;
                    if (pulses.length > 50) {
                        pulses.shift();
                        this.setState({
                            pulses: [...pulses, this.random(105, 40)]
                        })
                    } else {
                        this.setState({
                            pulses: [...this.state.pulses, this.random(105, 40)]
                        })
                    }
                }, 1000)*/
    }

    random = (max, min) => Math.random() * (max - min) + min

    getChartData = () => {
        return {
            labels: this.state.pulses.map(pulse => pulse.toFixed(0)),
            datasets: [
                {
                    label: 'BPM',
                    data: this.state.pulses,
                    backgroundColor: ['rgba(255, 55, 67, 0.72)'],
                    borderColor: ['rgba(255, 55, 67, 1)'],
                    borderWidth: 2,
                    fill: true,
                    lineTension: 0.1
                }
            ],
            options: {
                animation: {
                    duration: 0, // general animation time
                },
                hover: {
                    animationDuration: 0, // duration of animations when hovering an item
                },
                responsiveAnimationDuration: 0,
                scales: {
                    xAxes: [{
                        display: false,
                        barPercentage: 1.3,
                        ticks: {
                            max: 3,
                        }
                    }, {
                        display: true,
                        ticks: {
                            autoSkip: false,
                            max: 4,
                        }
                    }],
                    yAxes: [{
                        ticks: {
                            beginAtZero: true
                        }
                    }]
                }
            },

        }
    }


    render() {
        const beat = this.state.pulses ? this.state.pulses[0] : 0;
        return (
            <div style={{textAlign:'center', marginTop: 100}}>
                <Typography variant={"h1"} style={{color:'#797979'}}>{beat}</Typography>

                <Typography variant={"subtitle2"}>Device: Samsung S10+</Typography>
                <Typography variant={"subtitle2"}>Status: Connected since 2:25 PM</Typography>


                <Paper style={{marginTop:20, marginLeft:20, marginRight: 20}}>
                    <Line data={this.getChartData()} height={60}/>
                </Paper>
            </div>
        );
    }

}