export default (state = {
  patient: null,
  tags: [],
  activeTags: [],
  coordinatesByAddress: [],
  modal: {
    isOpen: false,
    operation: null
  }
}, action) => {
  switch (action.type) {
    case 'SET_PATIENTS':
      return {
        ...state,
        patients: action.payload
      };
    case 'SET_PATIENT':
      return {
        ...state,
        patient: {
          ...action.payload,
          tags: action.payload.tags.map((tag) => tag.name)
        }
      };
    case 'SET_TAGS':
      return {
        ...state,
        tags: action.payload
      };
    case 'SET_ACTIVE_TAGS':
      return {
        ...state,
        activeTags: action.payload
      };
    case 'SET_COORDINATES_BY_ADDRESS':
      return {
        ...state,
        coordinatesByAddress: action.payload
      };
    case 'ADD_PATIENT':
      return {
        ...state,
        patients: [...state.patients, action.payload]
      };
    case 'UPDATE_PATIENT':
      return {
        ...state,
        patient: {
          ...action.payload,
          tags: action.payload.tags.map((tag) => tag.name)
        },
        patients: [...state.patients.map((patient) => {
          if (patient.id === action.payload.id) {
            return action.payload;
          }
          return patient
        })]
      };
    case 'SHOW_MODAL':
      return {
        ...state,
        modal: {
          isOpen: true,
          operation: action.payload
        }
      };
    case 'HIDE_MODAL':
      return {
        ...state,
        modal: {
          isOpen: false,
          operation: null
        }
      };
    default:
      return state
  }
}
