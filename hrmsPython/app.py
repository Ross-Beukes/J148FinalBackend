import os

from flask import Flask, jsonify, render_template, request, send_file
import requests
from datetime import datetime
import base64
import io

from reportlab.lib.pagesizes import letter
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import SimpleDocTemplate, Image, Paragraph, Spacer
from reportlab.lib.styles import getSampleStyleSheet
from reportlab.platypus import PageBreak  # Add this import
from reportlab.lib.styles import ParagraphStyle
from reportlab.pdfbase import pdfmetrics
from reportlab.lib.colors import black

from data_analysis.data_processing import process_data_and_generate_graph
from models.ContractorPerformance import ContractorPerformance

app = Flask(__name__)

BACKEND_API = "http://localhost:8080/J148FinalBackend/api/contractor-performance"


@app.route('/')
def home():
    return render_template('index.html')



@app.route('/download-report')
def download_report():
    # Path to the Excel file that you want to send
    report_path = "restclients/downloaded_report.xlsx"


    # Make sure the file exists
    if os.path.exists(report_path):
        return send_file(report_path, as_attachment=True, download_name="Contractor_Performance_Report.xlsx", mimetype="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    else:
        return jsonify(error="Failed to download report"), 404

@app.route('/generate-pdf-report', methods=['POST'])
def generate_pdf_report():
    print("report")
    # Create a buffer for the PDF
    buffer = io.BytesIO()

    # Create the PDF document
    doc = SimpleDocTemplate(buffer, pagesize=letter)

    # Styles for the PDF
    styles = getSampleStyleSheet()

    # Create a custom style for descriptions


    # Register Calibri font (ensure you have the font file in your project)
    try:
        pdfmetrics.registerFont(TTFont('Calibri', 'Calibri.ttf'))
    except Exception as e:
        app.logger.warning(f"Could not register Calibri font: {e}. Falling back to default font.")

    desc_style = ParagraphStyle(
        'DescriptionStyle',
        parent=styles['Normal'],
        fontName='Helvetica',  # Fallback if Calibri is not available
        fontSize=12,
        textColor=black,
        leading=14,  # Adjust line spacing
        spaceBefore=12,  # Space before the paragraph
        spaceAfter=12   # Space after the paragraph
    )

    # List to hold the PDF content
    story = []

    # Add a title to the report
    title = Paragraph("Contractor Performance Analysis Report", styles['Title'])
    story.append(title)
    story.append(Spacer(1, 12))

    # Process each graph and its description
    graphs_count = len([key for key in request.form.keys() if key.startswith('description_')])

    for i in range(1, graphs_count + 1):
        # Get the graph image (Base64 encoded)
        graph_base64 = request.form.get(f'graph_{i}', '')
        description = request.form.get(f'description_{i}', '')

        if graph_base64:
            try:
                # Decode the base64 image
                image_data = base64.b64decode(graph_base64)

                # Create an in-memory image
                img_buffer = io.BytesIO(image_data)

                # Add graph title
                graph_title = Paragraph(f"Graph {i}", styles['Heading2'])
                story.append(graph_title)

                # Add more spacing before the image
                story.append(Spacer(1, 20))

                # Add the graph image
                img = Image(img_buffer, width=500, height=375)  # Increased size
                img.hAlign = 'CENTER'  # Center the image
                story.append(img)

                # Add more spacing after the image
                story.append(Spacer(1, 20))

                # Add description if provided, preserving line breaks
                if description.strip():
                    # Replace line breaks with paragraph breaks
                    formatted_description = description.replace('\n', '<br/>')
                    description_para = Paragraph(formatted_description, desc_style)
                    story.append(description_para)

                # Add spacing
                story.append(Spacer(1, 12))

                # Add page break (except for the last graph)
                if i < graphs_count:
                    story.append(PageBreak())

            except Exception as e:
                # Handle any decoding errors
                app.logger.error(f"Error processing graph {i}: {e}")

    # Build the PDF
    doc.build(story)

    # Move buffer to the beginning
    buffer.seek(0)

    # Send the PDF as a file download
    return send_file(
        buffer,
        as_attachment=True,
        download_name='contractor_performance_analysis.pdf',
        mimetype='application/pdf'
    )


@app.route('/get-all-contractor-performance')
def get_all_contractor_performance():
    print("heelp")
    try:
        response = requests.get(f"{BACKEND_API}/get-all-contractor-performance",
                                headers={"Content-Type": "application/json"})

        print(response)


        if response.status_code == 200:
            contractor_performance_list = []

            # Deserialize JSON data
            for item in response.json():
                # Parse datetime fields
                if 'aptitudeTest' in item and 'testDate' in item['aptitudeTest']:
                    item['aptitudeTest']['testDate'] = datetime.fromisoformat(item['aptitudeTest']['testDate'])

                for attendance in item.get('attendanceList', []):
                    attendance['timeIn'] = datetime.fromisoformat(attendance['timeIn'])
                    attendance['timeOut'] = datetime.fromisoformat(attendance['timeOut'])

                for hearing in item.get('hearingList', []):
                    hearing['scheduleDate'] = datetime.fromisoformat(hearing['scheduleDate'])

                for warning in item.get('warningList', []):
                    warning['dateIssue'] = datetime.fromisoformat(warning['dateIssue'])

                contractor_performance = ContractorPerformance.from_dict(item)
                contractor_performance_list.append(contractor_performance)

            # Generate graph
            graphs = process_data_and_generate_graph(contractor_performance_list)

            # Embed graph in an HTML page
            return render_template('graphs.html', graphs=graphs)

        else:
            app.logger.error(f"Failed to fetch data, status code: {response.status_code}")
            return "Error fetching data", 500

    except requests.exceptions.RequestException as e:
        app.logger.error(f"Request error: {e}")
        return "Error with the request", 500


if __name__ == '__main__':
    app.run(debug=True)