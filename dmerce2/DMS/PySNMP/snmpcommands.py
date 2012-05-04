"""
   Retrieve next lexicographically larger Object IDs starting from user
   specifed SNMP Object ID from arbitrary SNMP agent.

   Since MIB parser is not yet implemented in Python, this script takes and
   reports Object IDs in dotted numeric representation only.

   Written by Ilya Etingof <ilya@glas.net>, 10/2000
"""
import types
import string

import session
import error

class SnmpNumeric(session.session):
    """Browse remote SNMP process
    """
    def __init__(self, agent, community):
        """Explicitly call superclass's constructor as it gets
           overloaded by this class constructor and pass a few
           arguments alone.
        """   
        session.session.__init__(self, agent, community)

    def walk(self, objids):
        """Query SNMP agent for one or more Object IDs. The objid
           argument should be a list of strings where each string
           represents a Object ID in dotted numbers notation
           (e.g. ['.1.3.6.1.4.1.307.3.2.1.1.1.4.1']).
        """   
        # Convert string type Object IDs into numeric representation
        numeric_objids = map(self.str2nums, objids)

        # BER encode SNMP Object IDs to query
        encoded_objids = map(self.encode_oid, numeric_objids)

        #print 'numeric_objids = ', numeric_objids

        #Output of the readable OID
        i = 0
        oid_string = ''
        
        while i in range(len(numeric_objids[0])):
            oid_string = oid_string + '.' + str(int(numeric_objids[0][i]))
            i = i + 1
            
            
        # Because Index is starting from zero and len "from one"
        #place = len(numeric_objids[0]) - 2 
        #prelast_number = int(numeric_objids[0][place])
                
        # Since we are going to _query_ SNMP agent for Object IDs
        # associated value, there will be no variable values passed to
        # SNMP agent.
        encoded_values=[]
        
        # traverse the agent's MIB
        list = []
        while 1:
            # Build a complete SNMP message of type 'GETNEXTREQUEST',
            # pass it a list BER encoded Object IDs to query and an
            # empty list of values associated with these Object IDs
            # (empty list as there is no point to pass any variables
            # values along the SNMP GETNEXT request)
            question = self.encode_request('GETNEXTREQUEST',\
                                           encoded_objids, encoded_values)

            # Try to send SNMP message to SNMP agent and receive a response.
            answer = self.send_and_receive(question)

            # Catch SNMP exceptions
            try:
                # As we get a response from SNMP agent, try to disassemble
                # SNMP reply and extract two lists of BER encoded SNMP
                # Object IDs and associated values).
                #print 'Answer :',self.decode_response(answer)       
                (encoded_objids, encoded_values)=self.decode_response(answer)

            # SNMP agent reports 'no such name' when walk is over
            except error.SNMPError, why:
                # If NoSuchName
                if why.status == 2:
                    # Return as we are done
                    return
                else:
                    raise error.SNMPError(why.status, why.index)
                
            # Decode BER encoded Object ID.
            objids = map(self.decode_value, encoded_objids)

            # Check if the Objid-Range is ok
            # Replace the first '.' poor having no '' in list returned
            my_objid_string = string.replace(objids[0],'.','',1)
            # Return list my_objids
            my_objids = string.split(my_objid_string, '.')
            i=0
            my_oid_string = ''
            # Get the prefix of the actual Objid String
            #print 'RANGE',range(len(numeric_objids[0]))
            while i in range(len(numeric_objids[0])):
                my_oid_string = my_oid_string + '.' + str(int(my_objids[i]))
                i = i + 1
            # If the actual Objidprefix differs to expected break
            #print 'OID-STRING :', oid_string
            #print 'ACT STRING :', my_oid_string
            if not my_oid_string == oid_string:
                break
            

            # Decode BER encoded values associated with Object IDs.
            values = map(self.decode_value, encoded_values)
            
            # convert two lists into a list of tuples for easier printing
            #########results = map(None, objids, values)
	    
            # Just print them out
            #for (objid, value) in results:
            ##########    print objid + ' ---> ' + str(value)

            #list.append(str(objids[0]),str(values))
            list.append(str(objids[0]),values[0])
        return list

    def get (self, objids):
        """Query SNMP agent for one or more Object IDs. The objid
           argument should be a list of strings where each string
           represents a Object ID in dotted numbers notation
           (e.g. ['.1.3.6.1.4.1.307.3.2.1.1.1.4.1']).
        """   
        # Convert string type Object IDs into numeric representation
        numeric_objids = map (self.str2nums, objids)

        # BER encode SNMP Object IDs to query
        encoded_objids = map (self.encode_oid, numeric_objids)

        # Since we are going to _query_ SNMP agent for Object IDs
        # associated value, there will be no variable values passed to
        # SNMP agent.
        encoded_values = []
        
        # Build a complete SNMP message of type 'GETREQUEST', pass it a list
        # of BER encoded Object IDs to query and an empty list of values 
        # associated with these Object IDs (empty list as there is no point
        # to pass any variables values along the SNMP GET request)
        question = self.encode_request ('GETREQUEST', encoded_objids,\
                                        encoded_values)

        # Try to send SNMP message to SNMP agent and receive a response.
        answer = self.send_and_receive (question)

        # As we get a response from SNMP agent, try to disassemble SNMP reply
        # and extract two lists of BER encoded SNMP Object IDs and 
        # associated values).
        (encoded_objids, encoded_values) = self.decode_response (answer)

        # Decode BER encoded Object ID.
        objids = map (self.decode_value, encoded_objids)

        # Decode BER encoded values associated with Object IDs.
        values = map (self.decode_value, encoded_values)

        # Return a tuble of two lists holding Object IDs and associated
        # values extracted from SNMP agent reply.
        return (objids, values)







