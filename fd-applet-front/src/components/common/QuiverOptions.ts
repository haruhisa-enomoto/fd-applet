import {
  Options,
} from "react-vis-graph-wrapper";

import { blue, grey, purple, red } from "@mui/material/colors";

export const defaultOptions: Options = {
  nodes: {
    font: {
      face: "roboto",
    },
    color: {
      background: grey[100],
      border: grey[600],
      highlight: {
        background: blue[100],
        border: blue[600],
      },
    },
  },
  edges: {
    chosen: false,
    arrows: "to",
    color: { inherit: false },
    font: {
      face: "roboto",
      size: 30,
    },
    width: 3,
    smooth: {
      enabled: true,
      type: "dynamic",
      roundness: 0.5,
    },
    // selectionWidth: 0,
  },
  physics: {
    solver: "barnesHut",
    // solver: "forceAtlas2Based",
    barnesHut: {
      theta: 1,
      springConstant: 0.05,
      // springLength: 200,
      // gravitationalConstant: -20000,
    },
  },
  layout: {
    // improvedLayout: false,
  },
  groups: {
    default: {
      color: {
        background: grey[100],
        border: grey[600],
      },
    },
    groupOne: {
      color: {
        background: blue[100],
        border: blue[600],
      },
    },
    groupTwo: {
      color: {
        background: red[100],
        border: red[600],
      },
    },
    groupBoth: {
      color: {
        background: purple[100],
        border: purple[600],
      },
    },
    highlight: {
      color: {
        background: red[100],
        border: red[600],
      },
    },
  },
};

export const defaultPhysicsOptions = {
  solver: "barnesHut",
  // solver: "forceAtlas2Based",
  barnesHut: {
    theta: 1,
    springConstant: 0.05,
    springLength: 95,
    gravitationalConstant: -2000,
  },
};
