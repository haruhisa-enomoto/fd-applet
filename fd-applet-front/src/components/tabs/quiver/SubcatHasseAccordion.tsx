import { useState } from "react";

import { GraphEvents } from "react-vis-graph-wrapper";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Divider,
  Typography,
} from "@mui/material";

import { Combo } from "../../common/Combo";
import GenericQuiver from "../../common/GenericQuiver";
import { subcatOptions } from "../enumerator/SubcatEnumerator";

import { HassePhysicsOption } from "./QuiversTab";

interface SubcatHasseAccordionProps {
  events: GraphEvents;
}

export default function SubcatHasseAccordion({
  events,
}: SubcatHasseAccordionProps) {
  const [selectedMenu, setSelectedMenu] = useState("tors");

  return (
    <Accordion TransitionProps={{ unmountOnExit: true }}>
      <AccordionSummary expandIcon={<ExpandMoreIcon />} sx={{ m: 0 }}>
        <Typography>Hasse quiver of subcategories</Typography>
      </AccordionSummary>
      <Divider />
      <AccordionDetails>
        <Combo
          title="Choose types"
          options={subcatOptions}
          selected={selectedMenu}
          setSelected={setSelectedMenu}
        />

        <GenericQuiver
          url={"/api/quiver/subcat/" + selectedMenu}
          physicOption={HassePhysicsOption}
          updateButton
          allowChosen
          events={events}
        />
      </AccordionDetails>
    </Accordion>
  );
}
