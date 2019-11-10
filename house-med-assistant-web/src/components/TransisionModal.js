import React from 'react';
import {makeStyles} from '@material-ui/core/styles';
import Modal from '@material-ui/core/Modal';
import Backdrop from '@material-ui/core/Backdrop';
import Fade from '@material-ui/core/Fade';
import {cleanCoordinates, hideModal} from "../actions/patientActions";
import {connect} from "react-redux";
import AddNewPatientModalContent from "./AddNewPatientModalContent";
import EditPatientModalContent from "./EditPatientModalContent";

const useStyles = makeStyles(theme => ({
  modal: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  paper: {
    backgroundColor: theme.palette.background,
    boxShadow: theme.shadows[5],
    padding: theme.spacing(2, 4, 3),
    width: '60%',
    minWidth: '100px',
  },
}));

const mapStateToProps = (state) => {
  return {
    patientModal: state.patientReducer.modal,
  }
};

const mapDispatchToProps = dispatch => ({
  hideModal: () => dispatch(hideModal()),
  cleanCoordinates: () => dispatch(cleanCoordinates()),
});


export default connect(mapStateToProps, mapDispatchToProps)(function TransisionModal(props) {
    const classes = useStyles();

    const handleClose = () => {
      props.hideModal();
      props.cleanCoordinates();
    };

    console.log("render modal");
    return (
      <Modal
        aria-labelledby="transition-modal-title"
        aria-describedby="transition-modal-description"
        className={classes.modal}
        open={props.patientModal.isOpen}
        onClose={handleClose}
        closeAfterTransition
        BackdropComponent={Backdrop}
        BackdropProps={{
          timeout: 500,
        }}
      >
        <Fade in={props.patientModal.isOpen}>
          <div className={classes.paper}>


            {props.patientModal.operation === "add" ?
              <h2 id="transition-modal-title">Dodaj pacjenta</h2>
              : <h2 id="transition-modal-title">Edytuj pacjenta</h2>
            }
            {/*<p id="transition-modal-description">react-transition-group animates me.</p>*/}
            {props.patientModal.operation === "add" ? <AddNewPatientModalContent/>
              : <EditPatientModalContent patientId={props.patientModal.operation}/>}

          </div>
        </Fade>
      </Modal>
    );
  }
)
